package com.caps.eteeapp.controller;

import com.caps.eteeapp.model.Message;
import com.caps.eteeapp.model.Applicant;
import com.caps.eteeapp.model.Evaluator;
import com.caps.eteeapp.model.ProgramAdmin;
import com.caps.eteeapp.service.MessageService;
import com.caps.eteeapp.repository.ApplicantRepository;
import com.caps.eteeapp.repository.EvaluatorRepository;
import com.caps.eteeapp.repository.ProgramAdminRepository;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.Comparator;
import java.util.ArrayList;
import java.util.HashMap;

@RestController
@RequestMapping("/api/messages")
public class MessageController {

    private final MessageService messageService;
    private final ApplicantRepository applicantRepository;
    private final EvaluatorRepository evaluatorRepository;
    private final ProgramAdminRepository programAdminRepository;

    public MessageController(
        MessageService messageService,
        ApplicantRepository applicantRepository,
        EvaluatorRepository evaluatorRepository,
        ProgramAdminRepository programAdminRepository
    ) {
        this.messageService = messageService;
        this.applicantRepository = applicantRepository;
        this.evaluatorRepository = evaluatorRepository;
        this.programAdminRepository = programAdminRepository;
    }

    @PostMapping("/send")
    public Message sendMessage(@RequestBody Message message) {
        return messageService.sendMessage(message);
    }

    @GetMapping("/conversation/applicant-evaluator")
    public List<Message> getApplicantEvaluatorConversation(
        @RequestParam Long applicantId,
        @RequestParam Long evaluatorId
    ) {
        Applicant applicant = applicantRepository.findById(applicantId).orElse(null);
        Evaluator evaluator = evaluatorRepository.findById(evaluatorId).orElse(null);
        return messageService.getApplicantEvaluatorConversation(applicant, evaluator);
    }

    @GetMapping("/conversation/applicant-admin")
    public List<Message> getApplicantAdminConversation(
        @RequestParam Long applicantId,
        @RequestParam Long adminId
    ) {
        Applicant applicant = applicantRepository.findById(applicantId).orElse(null);
        ProgramAdmin admin = programAdminRepository.findById(adminId).orElse(null);
        return messageService.getApplicantAdminConversation(applicant, admin);
    }

    @GetMapping("/conversation/evaluator-admin")
    public List<Message> getEvaluatorAdminConversation(
        @RequestParam Long evaluatorId,
        @RequestParam Long adminId
    ) {
        Evaluator evaluator = evaluatorRepository.findById(evaluatorId).orElse(null);
        ProgramAdmin admin = programAdminRepository.findById(adminId).orElse(null);
        return messageService.getEvaluatorAdminConversation(evaluator, admin);
    }

    @GetMapping("/inbox/applicant")
    public List<Message> getApplicantInbox(@RequestParam Long applicantId) {
        Applicant applicant = applicantRepository.findById(applicantId).orElse(null);
        return messageService.getApplicantInbox(applicant);
    }

    @GetMapping("/inbox/evaluator")
    public List<Message> getEvaluatorInbox(@RequestParam Long evaluatorId) {
        Evaluator evaluator = evaluatorRepository.findById(evaluatorId).orElse(null);
        return messageService.getEvaluatorInbox(evaluator);
    }

    @GetMapping("/inbox/admin")
    public List<Message> getAdminInbox(@RequestParam Long adminId) {
        ProgramAdmin admin = programAdminRepository.findById(adminId).orElse(null);
        return messageService.getAdminInbox(admin);
    }

    @DeleteMapping("/{messageId}")
    public void deleteMessage(@PathVariable Long messageId) {
        messageService.deleteMessage(messageId);
    }

    @GetMapping("/inbox/applicant/chat-list")
    public ResponseEntity<List<Map<String, Object>>> getApplicantChatList(@RequestParam Long applicantId) {
        Applicant applicant = applicantRepository.findById(applicantId).orElse(null);
        if (applicant == null) {
            return ResponseEntity.badRequest().body(new ArrayList<>());
        }
        List<Message> allMessages = new ArrayList<>();
        allMessages.addAll(messageService.getApplicantInbox(applicant));
        allMessages.addAll(messageService.getApplicantAdminConversation(applicant, null)); // fallback for admin-initiated
        allMessages.addAll(messageService.getApplicantEvaluatorConversation(applicant, null)); // fallback for evaluator-initiated

        // Collect all messages where applicant is either sender or recipient
        allMessages = allMessages.stream()
            .filter(m -> (m.getSenderApplicant() != null && m.getSenderApplicant().getApplicantId().equals(applicantId)) ||
                         (m.getRecipientApplicant() != null && m.getRecipientApplicant().getApplicantId().equals(applicantId)))
            .collect(Collectors.toList());

        // Group by conversation partner (evaluator or admin)
        Map<String, List<Message>> conversationMap = new HashMap<>();
        for (Message m : allMessages) {
            String key = null;
            String role = null;
            String name = null;
            Long participantId = null;
            if (m.getSenderApplicant() != null && m.getSenderApplicant().getApplicantId().equals(applicantId)) {
                // applicant is sender, so group by recipient
                if (m.getRecipientEvaluator() != null) {
                    key = "EVALUATOR_" + m.getRecipientEvaluator().getEvaluatorId();
                    role = "EVALUATOR";
                    name = m.getRecipientEvaluator().getName();
                    participantId = m.getRecipientEvaluator().getEvaluatorId();
                } else if (m.getRecipientAdmin() != null) {
                    key = "ADMIN_" + m.getRecipientAdmin().getAdminId();
                    role = "PROGRAM_ADMIN";
                    name = m.getRecipientAdmin().getName();
                    participantId = m.getRecipientAdmin().getAdminId();
                }
            } else {
                // applicant is recipient, so group by sender
                if (m.getSenderEvaluator() != null) {
                    key = "EVALUATOR_" + m.getSenderEvaluator().getEvaluatorId();
                    role = "EVALUATOR";
                    name = m.getSenderEvaluator().getName();
                    participantId = m.getSenderEvaluator().getEvaluatorId();
                } else if (m.getSenderAdmin() != null) {
                    key = "ADMIN_" + m.getSenderAdmin().getAdminId();
                    role = "PROGRAM_ADMIN";
                    name = m.getSenderAdmin().getName();
                    participantId = m.getSenderAdmin().getAdminId();
                }
            }
            if (key != null) {
                conversationMap.computeIfAbsent(key, k -> new ArrayList<>()).add(m);
            }
        }

        List<Map<String, Object>> chatList = new ArrayList<>();
        for (Map.Entry<String, List<Message>> entry : conversationMap.entrySet()) {
            List<Message> messages = entry.getValue();
            messages.sort(Comparator.comparing(Message::getSentAt).reversed());
            Message lastMessage = messages.get(0);

            String role = null;
            String name = null;
            Long participantId = null;
            if (lastMessage.getSenderApplicant() != null && lastMessage.getSenderApplicant().getApplicantId().equals(applicantId)) {
                if (lastMessage.getRecipientEvaluator() != null) {
                    role = "EVALUATOR";
                    name = lastMessage.getRecipientEvaluator().getName();
                    participantId = lastMessage.getRecipientEvaluator().getEvaluatorId();
                } else if (lastMessage.getRecipientAdmin() != null) {
                    role = "PROGRAM_ADMIN";
                    name = lastMessage.getRecipientAdmin().getName();
                    participantId = lastMessage.getRecipientAdmin().getAdminId();
                }
            } else {
                if (lastMessage.getSenderEvaluator() != null) {
                    role = "EVALUATOR";
                    name = lastMessage.getSenderEvaluator().getName();
                    participantId = lastMessage.getSenderEvaluator().getEvaluatorId();
                } else if (lastMessage.getSenderAdmin() != null) {
                    role = "PROGRAM_ADMIN";
                    name = lastMessage.getSenderAdmin().getName();
                    participantId = lastMessage.getSenderAdmin().getAdminId();
                }
            }

            boolean unread = messages.stream().anyMatch(m ->
                m.getRecipientApplicant() != null &&
                m.getRecipientApplicant().getApplicantId().equals(applicantId)
                // && !Boolean.TRUE.equals(m.getRead())
            );

            Map<String, Object> chatItem = new HashMap<>();
            chatItem.put("participantId", participantId);
            chatItem.put("participantName", name);
            chatItem.put("participantRole", role);
            chatItem.put("lastMessageContent", lastMessage.getContent());
            chatItem.put("lastMessageTimestamp", lastMessage.getSentAt());
            chatItem.put("unread", unread);

            chatList.add(chatItem);
        }

        // Sort by last message timestamp descending
        chatList.sort((a, b) -> {
            Object t1 = a.get("lastMessageTimestamp");
            Object t2 = b.get("lastMessageTimestamp");
            if (t1 == null && t2 == null) return 0;
            if (t1 == null) return 1;
            if (t2 == null) return -1;
            return ((Comparable) t2).compareTo(t1);
        });

        return ResponseEntity.ok(chatList);
    }

    @GetMapping("/inbox/evaluator/chat-list")
    public ResponseEntity<List<Map<String, Object>>> getEvaluatorChatList(@RequestParam Long evaluatorId) {
        Evaluator evaluator = evaluatorRepository.findById(evaluatorId).orElse(null);
        if (evaluator == null) {
            return ResponseEntity.badRequest().body(new ArrayList<>());
        }
        List<Message> allMessages = new ArrayList<>();
        allMessages.addAll(messageService.getEvaluatorInbox(evaluator));
        allMessages.addAll(messageService.getApplicantEvaluatorConversation(null, evaluator)); // fallback for applicant-initiated
        allMessages.addAll(messageService.getEvaluatorAdminConversation(evaluator, null)); // fallback for admin-initiated

        // Collect all messages where evaluator is either sender or recipient
        allMessages = allMessages.stream()
            .filter(m -> (m.getSenderEvaluator() != null && m.getSenderEvaluator().getEvaluatorId().equals(evaluatorId)) ||
                         (m.getRecipientEvaluator() != null && m.getRecipientEvaluator().getEvaluatorId().equals(evaluatorId)))
            .collect(Collectors.toList());

        // Group by conversation partner (applicant or admin)
        Map<String, List<Message>> conversationMap = new HashMap<>();
        for (Message m : allMessages) {
            String key = null;
            String role = null;
            String name = null;
            Long participantId = null;
            if (m.getSenderEvaluator() != null && m.getSenderEvaluator().getEvaluatorId().equals(evaluatorId)) {
                // evaluator is sender, so group by recipient
                if (m.getRecipientApplicant() != null) {
                    key = "APPLICANT_" + m.getRecipientApplicant().getApplicantId();
                    role = "APPLICANT";
                    name = m.getRecipientApplicant().getFirstName() + " " + m.getRecipientApplicant().getLastName();
                    participantId = m.getRecipientApplicant().getApplicantId();
                } else if (m.getRecipientAdmin() != null) {
                    key = "ADMIN_" + m.getRecipientAdmin().getAdminId();
                    role = "PROGRAM_ADMIN";
                    name = m.getRecipientAdmin().getName();
                    participantId = m.getRecipientAdmin().getAdminId();
                }
            } else {
                // evaluator is recipient, so group by sender
                if (m.getSenderApplicant() != null) {
                    key = "APPLICANT_" + m.getSenderApplicant().getApplicantId();
                    role = "APPLICANT";
                    name = m.getSenderApplicant().getFirstName() + " " + m.getSenderApplicant().getLastName();
                    participantId = m.getSenderApplicant().getApplicantId();
                } else if (m.getSenderAdmin() != null) {
                    key = "ADMIN_" + m.getSenderAdmin().getAdminId();
                    role = "PROGRAM_ADMIN";
                    name = m.getSenderAdmin().getName();
                    participantId = m.getSenderAdmin().getAdminId();
                }
            }
            if (key != null) {
                conversationMap.computeIfAbsent(key, k -> new ArrayList<>()).add(m);
            }
        }

        List<Map<String, Object>> chatList = new ArrayList<>();
        for (Map.Entry<String, List<Message>> entry : conversationMap.entrySet()) {
            List<Message> messages = entry.getValue();
            messages.sort(Comparator.comparing(Message::getSentAt).reversed());
            Message lastMessage = messages.get(0);

            String role = null;
            String name = null;
            Long participantId = null;
            if (lastMessage.getSenderEvaluator() != null && lastMessage.getSenderEvaluator().getEvaluatorId().equals(evaluatorId)) {
                if (lastMessage.getRecipientApplicant() != null) {
                    role = "APPLICANT";
                    name = lastMessage.getRecipientApplicant().getFirstName() + " " + lastMessage.getRecipientApplicant().getLastName();
                    participantId = lastMessage.getRecipientApplicant().getApplicantId();
                } else if (lastMessage.getRecipientAdmin() != null) {
                    role = "PROGRAM_ADMIN";
                    name = lastMessage.getRecipientAdmin().getName();
                    participantId = lastMessage.getRecipientAdmin().getAdminId();
                }
            } else {
                if (lastMessage.getSenderApplicant() != null) {
                    role = "APPLICANT";
                    name = lastMessage.getSenderApplicant().getFirstName() + " " + lastMessage.getSenderApplicant().getLastName();
                    participantId = lastMessage.getSenderApplicant().getApplicantId();
                } else if (lastMessage.getSenderAdmin() != null) {
                    role = "PROGRAM_ADMIN";
                    name = lastMessage.getSenderAdmin().getName();
                    participantId = lastMessage.getSenderAdmin().getAdminId();
                }
            }

            // Unread: any message in this conversation where evaluator is recipient
            boolean unread = messages.stream().anyMatch(m ->
                m.getRecipientEvaluator() != null &&
                m.getRecipientEvaluator().getEvaluatorId().equals(evaluatorId)
                // && !Boolean.TRUE.equals(m.getRead()) // Uncomment if you add a 'read' field
            );

            Map<String, Object> chatItem = new HashMap<>();
            chatItem.put("participantId", participantId);
            chatItem.put("participantName", name);
            chatItem.put("participantRole", role);
            chatItem.put("lastMessageContent", lastMessage.getContent());
            chatItem.put("lastMessageTimestamp", lastMessage.getSentAt());
            chatItem.put("unread", unread);

            chatList.add(chatItem);
        }

        // Sort by last message timestamp descending
        chatList.sort((a, b) -> {
            Object t1 = a.get("lastMessageTimestamp");
            Object t2 = b.get("lastMessageTimestamp");
            if (t1 == null && t2 == null) return 0;
            if (t1 == null) return 1;
            if (t2 == null) return -1;
            return ((Comparable) t2).compareTo(t1);
        });

        return ResponseEntity.ok(chatList);
    }

    @GetMapping("/inbox/admin/chat-list")
    public ResponseEntity<List<Map<String, Object>>> getAdminChatList(@RequestParam Long adminId) {
        ProgramAdmin admin = programAdminRepository.findById(adminId).orElse(null);
        if (admin == null) {
            return ResponseEntity.badRequest().body(new ArrayList<>());
        }
        List<Message> allMessages = new ArrayList<>();
        allMessages.addAll(messageService.getAdminInbox(admin));
        allMessages.addAll(messageService.getApplicantAdminConversation(null, admin)); // fallback for applicant-initiated
        allMessages.addAll(messageService.getEvaluatorAdminConversation(null, admin)); // fallback for evaluator-initiated

        // Collect all messages where admin is either sender or recipient
        allMessages = allMessages.stream()
            .filter(m -> (m.getSenderAdmin() != null && m.getSenderAdmin().getAdminId().equals(adminId)) ||
                         (m.getRecipientAdmin() != null && m.getRecipientAdmin().getAdminId().equals(adminId)))
            .collect(Collectors.toList());

        // Group by conversation partner (applicant or evaluator)
        Map<String, List<Message>> conversationMap = new HashMap<>();
        for (Message m : allMessages) {
            String key = null;
            String role = null;
            String name = null;
            Long participantId = null;
            if (m.getSenderAdmin() != null && m.getSenderAdmin().getAdminId().equals(adminId)) {
                // admin is sender, so group by recipient
                if (m.getRecipientApplicant() != null) {
                    key = "APPLICANT_" + m.getRecipientApplicant().getApplicantId();
                    role = "APPLICANT";
                    name = m.getRecipientApplicant().getFirstName() + " " + m.getRecipientApplicant().getLastName();
                    participantId = m.getRecipientApplicant().getApplicantId();
                } else if (m.getRecipientEvaluator() != null) {
                    key = "EVALUATOR_" + m.getRecipientEvaluator().getEvaluatorId();
                    role = "EVALUATOR";
                    name = m.getRecipientEvaluator().getName();
                    participantId = m.getRecipientEvaluator().getEvaluatorId();
                }
            } else {
                // admin is recipient, so group by sender
                if (m.getSenderApplicant() != null) {
                    key = "APPLICANT_" + m.getSenderApplicant().getApplicantId();
                    role = "APPLICANT";
                    name = m.getSenderApplicant().getFirstName() + " " + m.getSenderApplicant().getLastName();
                    participantId = m.getSenderApplicant().getApplicantId();
                } else if (m.getSenderEvaluator() != null) {
                    key = "EVALUATOR_" + m.getSenderEvaluator().getEvaluatorId();
                    role = "EVALUATOR";
                    name = m.getSenderEvaluator().getName();
                    participantId = m.getSenderEvaluator().getEvaluatorId();
                }
            }
            if (key != null) {
                // Attach participantId to the first message in the group for later use
                m.getClass().getDeclaredFields(); // dummy line to avoid unused warning
                m.getClass(); // dummy line to avoid unused warning
                m.getClass(); // dummy line to avoid unused warning
                conversationMap.computeIfAbsent(key, k -> new ArrayList<>()).add(m);
                // We'll set participantId in the chat item below
            }
        }

        List<Map<String, Object>> chatList = new ArrayList<>();
        for (Map.Entry<String, List<Message>> entry : conversationMap.entrySet()) {
            List<Message> messages = entry.getValue();
            messages.sort(Comparator.comparing(Message::getSentAt).reversed());
            Message lastMessage = messages.get(0);

            String role = null;
            String name = null;
            Long participantId = null;
            if (lastMessage.getSenderAdmin() != null && lastMessage.getSenderAdmin().getAdminId().equals(adminId)) {
                if (lastMessage.getRecipientApplicant() != null) {
                    role = "APPLICANT";
                    name = lastMessage.getRecipientApplicant().getFirstName() + " " + lastMessage.getRecipientApplicant().getLastName();
                    participantId = lastMessage.getRecipientApplicant().getApplicantId();
                } else if (lastMessage.getRecipientEvaluator() != null) {
                    role = "EVALUATOR";
                    name = lastMessage.getRecipientEvaluator().getName();
                    participantId = lastMessage.getRecipientEvaluator().getEvaluatorId();
                }
            } else {
                if (lastMessage.getSenderApplicant() != null) {
                    role = "APPLICANT";
                    name = lastMessage.getSenderApplicant().getFirstName() + " " + lastMessage.getSenderApplicant().getLastName();
                    participantId = lastMessage.getSenderApplicant().getApplicantId();
                } else if (lastMessage.getSenderEvaluator() != null) {
                    role = "EVALUATOR";
                    name = lastMessage.getSenderEvaluator().getName();
                    participantId = lastMessage.getSenderEvaluator().getEvaluatorId();
                }
            }

            boolean unread = messages.stream().anyMatch(m ->
                m.getRecipientAdmin() != null &&
                m.getRecipientAdmin().getAdminId().equals(adminId)
                // && !Boolean.TRUE.equals(m.getRead())
            );

            Map<String, Object> chatItem = new HashMap<>();
            chatItem.put("participantId", participantId);
            chatItem.put("participantName", name);
            chatItem.put("participantRole", role);
            chatItem.put("lastMessageContent", lastMessage.getContent());
            chatItem.put("lastMessageTimestamp", lastMessage.getSentAt());
            chatItem.put("unread", unread);

            chatList.add(chatItem);
        }

        chatList.sort((a, b) -> {
            Object t1 = a.get("lastMessageTimestamp");
            Object t2 = b.get("lastMessageTimestamp");
            if (t1 == null && t2 == null) return 0;
            if (t1 == null) return 1;
            if (t2 == null) return -1;
            return ((Comparable) t2).compareTo(t1);
        });

        return ResponseEntity.ok(chatList);
    }

    // To use this endpoint in Postman:

    // 1. Set the method to GET.
    // 2. Use the URL (replace with your actual port and IDs):
    //    http://localhost:{port}/api/messages/conversation/applicant-admin?applicantId={applicantId}&adminId={adminId}
    //    Example:
    //    http://localhost:8080/api/messages/conversation/applicant-admin?applicantId=102&adminId=1
    // 3. No request body is needed.
    // 4. Click "Send".
    // 5. The response will be a JSON array of messages between the applicant and the program admin.

    // To test the evaluator chat list endpoint in Postman:

    // 1. Set the method to GET.
    // 2. Use the URL (replace with your actual port and evaluatorId):
    //    http://localhost:{port}/api/messages/inbox/evaluator/chat-list?evaluatorId={evaluatorId}
    //    Example:
    //    http://localhost:8080/api/messages/inbox/evaluator/chat-list?evaluatorId=1
    // 3. No request body is needed.
    // 4. Click "Send".
    // 5. The response will be a JSON array of chat list items for the evaluator, each containing:
    //    - participantName
    //    - participantRole
    //    - lastMessageContent
    //    - lastMessageTimestamp
    //    - unread

    @GetMapping("/evaluators/all")
    public ResponseEntity<List<Map<String, Object>>> getAllEvaluators() {
        List<Evaluator> evaluators = evaluatorRepository.findAll();
        List<Map<String, Object>> evaluatorList = evaluators.stream()
            .map(evaluator -> {
                Map<String, Object> evaluatorData = new HashMap<>();
                evaluatorData.put("evaluatorId", evaluator.getEvaluatorId());
                evaluatorData.put("name", evaluator.getName());
                evaluatorData.put("email", evaluator.getEmail());
                return evaluatorData;
            })
            .collect(Collectors.toList());
        return ResponseEntity.ok(evaluatorList);
    }

    @GetMapping("/program-admins/all")
    public ResponseEntity<List<Map<String, Object>>> getAllProgramAdmins() {
        List<ProgramAdmin> admins = programAdminRepository.findAll();
        List<Map<String, Object>> adminList = admins.stream()
            .map(admin -> {
                Map<String, Object> adminData = new HashMap<>();
                adminData.put("adminId", admin.getAdminId());
                adminData.put("name", admin.getFirstName() + " " + admin.getLastName());
                adminData.put("email", admin.getEmail());
                return adminData;
            })
            .collect(Collectors.toList());
        return ResponseEntity.ok(adminList);
    }
}
