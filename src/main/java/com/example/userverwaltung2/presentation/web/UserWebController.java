package com.example.userverwaltung2.presentation.web;

import com.example.userverwaltung2.domain.*;
import com.example.userverwaltung2.persistance.AntwortRepository;
import com.example.userverwaltung2.persistance.ClientRepository;
import com.example.userverwaltung2.persistance.FragenRepository;
import com.example.userverwaltung2.presentation.web.dto.LoginDTO;
import com.example.userverwaltung2.presentation.web.dto.RegisterDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.time.LocalDate;
import java.util.*;

@Controller
public record UserWebController(ClientRepository clientRepository, FragenRepository fragenRepository,
                                PasswordEncoder passwordEncoder, UserDetailsService userDetailsService,
                                AntwortRepository antwortRepository) {
    private static final String LOGINDTO = "loginDTO";
    private static final String REGISTERDTO = "registerDTO";
    private static final String CLIENTNAME = "name";
    private static final String FRAGE = "frage";


    @GetMapping("/login")
    public String displayLogin() {
        return "login";
    }

    private void login(LoginDTO clientDTO, HttpServletRequest request) {
        var userDetails = userDetailsService.loadUserByUsername(clientDTO.getEmail());
        System.out.println(userDetails.getPassword());
        var auth = new UsernamePasswordAuthenticationToken(userDetails.getUsername(), userDetails.getPassword(), userDetails.getAuthorities());
        var securityContext = SecurityContextHolder.getContext();
        securityContext.setAuthentication(auth);
        var session = request.getSession(true);
        session.setAttribute("SPRING_SECURITY_CONTEXT", securityContext);
    }

    @GetMapping("/register")
    public String displayRegister(Model model) {
        if (!model.containsAttribute(REGISTERDTO))
            model.addAttribute(REGISTERDTO, new RegisterDTO());
        return "register";
    }

    @PostMapping("register")
    public String register(@Valid @ModelAttribute(REGISTERDTO) RegisterDTO registerDTO,
                           BindingResult bindingResult,
                           Model model,
                           HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            return displayRegister(model);
        }
        var client = new Client(registerDTO.getEmail(), Rolle.USER, registerDTO.getPassword());
        clientRepository.save(client);
        login(new LoginDTO(registerDTO.getEmail(), registerDTO.getPassword()), request);
        return "redirect:/overview";
    }

    @GetMapping("/overview")
    public String overview(Model model, Principal principal) {
        model.addAttribute(CLIENTNAME, principal.getName());
        if (isAdmin(principal)) {
            Map<Frage, List<Antwort>> questions = getComputedQuestions();
            model.addAttribute("questions", questions);
            return "admin-overview";
        } else {
            var questions = fragenRepository.findAllFragenByUnattendedClient(LocalDate.now(), principal.getName());
            model.addAttribute("questions", questions);
            return "overview";
        }

    }

    private boolean isAdmin(Principal principal) {
        var client = clientRepository.findByEmail(principal.getName()).orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST));
        return client.getRolle().equals(Rolle.ADMIN);
    }

    @GetMapping("/admin")
    public String displayAdminPanel(Model model, Principal principal) {
        if (!model.containsAttribute(CLIENTNAME)) {
            model.addAttribute(CLIENTNAME, principal.getName());
        }
        if (!model.containsAttribute(FRAGE)) {
            model.addAttribute(FRAGE, new Frage());
        }
        model.addAttribute("today", LocalDate.now());
        return "admin";
    }

    private Map<Frage, List<Antwort>> getComputedQuestions() {
        var rawAnswers = antwortRepository.findAll();

        Map<Frage, List<Antwort>> questions = rawAnswers.stream().reduce(
                new HashMap<Frage, List<Antwort>>(),
                (map, answer) -> {
                    map.putIfAbsent(answer.getFrage(), new LinkedList<>());
                    map.get(answer.getFrage()).add(answer);
                    return map;
                },
                (map1, map2) -> {
                    map2.forEach((key, value) ->
                            map1.merge(key, value, (v1, v2) -> {
                                v1.addAll(v2);
                                return v1;
                            }));
                    return map1;
                }

        );
        var allQuestions = fragenRepository.findAll();
        for (Frage q : allQuestions) {
            questions.putIfAbsent(q, List.of());
        }
        return questions;
    }

    @PostMapping("/admin/add-question")
    public String addQuestion(@Valid @ModelAttribute(FRAGE) Frage frage, BindingResult bindingResult, Model model, Principal principal) {
        if (bindingResult.hasErrors())
            return displayAdminPanel(model, principal);

        fragenRepository.save(frage);

        return "redirect:/overview";
    }

    @GetMapping("/question/{fragenID}")
    public String displayQuestion(Model model, @PathVariable("fragenID") Long fragenID, Principal principal) {
        var frage = fragenRepository.findById(fragenID).orElseThrow();
        var client = clientRepository.findByEmail(principal.getName()).orElseThrow();
        model.addAttribute(CLIENTNAME, client.getEmail());
        model.addAttribute("frage", frage);

        if (isAdmin(principal)) {
            Map<AntwortMoeglichkeiten, List<Antwort>> answers = rankedAnswers(antwortRepository.findAllByFrage(frage));
            model.addAttribute("antworten", answers);
            return "admin-question";
        } else {
            var alreadyAttended = antwortRepository.findByClientAndFrage(client, frage);
            if (alreadyAttended.isPresent()) {
                return "redirect:/overview";
            }

            model.addAttribute("antwort", new Antwort());
            model.addAttribute("antwortMoeglichkeiten", AntwortMoeglichkeiten.values());
            return "question";
        }
    }

    private Map<AntwortMoeglichkeiten, List<Antwort>> rankedAnswers(Set<Antwort> answers) {
        Map<AntwortMoeglichkeiten, List<Antwort>> computedAnswers = answers.stream().reduce(
                new HashMap<AntwortMoeglichkeiten, List<Antwort>>(),
                (map, answer) -> {
                    map.putIfAbsent(answer.getClientAntwort(), new LinkedList<>());
                    map.get(answer.getClientAntwort()).add(answer);
                    return map;
                },
                (map1, map2) -> {
                    map2.forEach((key, value) ->
                            map1.merge(key, value, (v1, v2) -> {
                                v1.addAll(v2);
                                return v1;
                            }));
                    return map1;
                }
        );
        var allAntwortMoegl = AntwortMoeglichkeiten.values();
        for (AntwortMoeglichkeiten moeglichkeiten : allAntwortMoegl) {
            computedAnswers.putIfAbsent(moeglichkeiten, List.of());
        }
        return computedAnswers;
    }

    @PostMapping("/answer/{fragenID}")
    public String handleAnswer(Model model,
                               @Valid @ModelAttribute("antwort") Antwort antwort,
                               BindingResult bindingResult,
                               @PathVariable("fragenID") Long fragenID,
                               Principal principal) {

        if (bindingResult.hasErrors()) {
            return displayQuestion(model, fragenID, principal);
        }
        var client = clientRepository.findByEmail(principal.getName()).orElseThrow();
        antwort.setClient(client);
        var frage = fragenRepository.findById(fragenID);
        if (frage.isEmpty()) {
            return "redirect:/overview";
        }
        var alreadyAttended = antwortRepository.findByClientAndFrage(client, frage.get());
        if (alreadyAttended.isEmpty()) {
            antwort.setFrage(frage.get());
            antwortRepository.save(antwort);
        }
        return "redirect:/overview";
    }


}
