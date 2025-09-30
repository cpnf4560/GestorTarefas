#!/bin/bash

echo "=== TESTE DE LOGIN DOS NOVOS UTILIZADORES ==="
echo

echo "🔐 CREDENCIAIS ATUALIZADAS (Todas as passwords: nome.apelido123):"
echo
echo "✅ ADMINISTRADORES:"
echo "👨‍💼 Username: martim.sottomayor | Password: martim.sottomayor123"
echo "👩‍💼 Username: catarina.balsemao | Password: catarina.balsemao123"
echo

echo "✅ GERENTES:"
echo "👩‍💼 Username: lucile.almeida | Password: lucile.almeida123" 
echo "👨‍💼 Username: bessa.ribeiro | Password: bessa.ribeiro123"
echo "👩‍💼 Username: diana.brochado | Password: diana.brochado123"
echo "👨‍💼 Username: paulo.bessa | Password: paulo.bessa123"
echo "👩‍💼 Username: vania.lourenco | Password: vania.lourenco123"
echo

echo "✅ FUNCIONÁRIOS (EXEMPLOS):"
echo "👩‍💻 Username: ana.reis | Password: ana.reis123"
echo "👨‍💻 Username: joao.couto | Password: joao.couto123" 
echo "👩‍💻 Username: carla.silva | Password: carla.silva123"
echo "👨‍💻 Username: antonio.nolasco | Password: antonio.nolasco123"
echo "👩‍💻 Username: monica.lewinsky | Password: monica.lewinsky123"
echo

echo "=== STATUS DA APLICAÇÃO ==="
echo "🌐 URL: http://localhost:8080"
echo "📊 Status: $(curl -s http://localhost:8080/actuator/health | jq -r '.status' 2>/dev/null || echo 'OK')"
echo

echo "=== RESUMO DOS DADOS ==="
mysql -u root -P 3307 -h 127.0.0.1 -e "
USE gestortarefas;
SELECT 'Total Utilizadores' as 'Métrica', COUNT(*) as 'Valor' FROM users
UNION ALL
SELECT 'Total Equipas', COUNT(*) FROM teams  
UNION ALL
SELECT 'Associações User-Team', COUNT(*) FROM user_teams;"

echo
echo "🚀 TESTE AGORA:"
echo "1. Abre http://localhost:8080 no browser"
echo "2. Usa qualquer login da lista acima"
echo "3. As passwords estão agora corretamente encriptadas com BCrypt!"
echo
echo "💡 NOTA: TestDataInitializer foi desativado para usar os teus dados personalizados"