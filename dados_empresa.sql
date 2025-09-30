-- Dados da empresa final - SHA-256 + salt
-- Password padrão: username + "123"
USE gestortarefas;

SELECT 'Sistema configurado com SHA-256 + salt' as status;
SELECT 'Passwords no formato: username + "123"' as info;
SELECT COUNT(*) as total_utilizadores FROM users;
SELECT COUNT(*) as total_equipas FROM teams;

-- Exemplos de login:
-- martim.sottomayor / martim.sottomayor123
-- catarina.balsemao / catarina.balsemao123  
-- lucile.almeida / lucile.almeida123
-- etc...