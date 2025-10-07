public class test_parsing {
    public static void main(String[] args) {
        String assignmentInfo = "Atribuída a: Martim Sottomayor | Equipa: Direção";
        
        // Testar getUserInfo
        if (assignmentInfo.contains("Atribuída a: ")) {
            String userPart = assignmentInfo.split("\\|")[0].trim();
            String user = userPart.replace("Atribuída a: ", "");
            System.out.println("User: " + user);
        }
        
        // Testar getTeamInfo
        if (assignmentInfo.contains("| Equipa: ")) {
            String[] parts = assignmentInfo.split("\\| Equipa: ");
            if (parts.length > 1) {
                String team = parts[1].trim();
                System.out.println("Team: " + team);
            }
        }
    }
}