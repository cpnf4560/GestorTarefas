-- MySQL dump 10.13  Distrib 8.0.43, for Linux (x86_64)
--
-- Host: localhost    Database: gestortarefas
-- ------------------------------------------------------
-- Server version	8.0.43-0ubuntu0.24.04.2

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `task_comments`
--

DROP TABLE IF EXISTS `task_comments`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `task_comments` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `comment_text` text NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `is_system_message` bit(1) NOT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `task_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK9517viwn2geh1gpivj6l9y64u` (`task_id`),
  KEY `FK6n4f8xnvwdkbjci078pqdn1w1` (`user_id`),
  CONSTRAINT `FK6n4f8xnvwdkbjci078pqdn1w1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
  CONSTRAINT `FK9517viwn2geh1gpivj6l9y64u` FOREIGN KEY (`task_id`) REFERENCES `tasks` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `task_comments`
--

LOCK TABLES `task_comments` WRITE;
/*!40000 ALTER TABLE `task_comments` DISABLE KEYS */;
/*!40000 ALTER TABLE `task_comments` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tasks`
--

DROP TABLE IF EXISTS `tasks`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tasks` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `actual_hours` int DEFAULT NULL,
  `completed_at` datetime(6) DEFAULT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `description` text,
  `due_date` datetime(6) DEFAULT NULL,
  `estimated_hours` int DEFAULT NULL,
  `priority` enum('ALTA','BAIXA','NORMAL','URGENTE') NOT NULL,
  `status` enum('CANCELADA','CONCLUIDA','EM_ANDAMENTO','PENDENTE') NOT NULL,
  `tags` varchar(255) DEFAULT NULL,
  `title` varchar(100) NOT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `assigned_by_user_id` bigint DEFAULT NULL,
  `assigned_team_id` bigint DEFAULT NULL,
  `created_by_user_id` bigint DEFAULT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKoe33h4u0gc5gsonwrqa5x84kn` (`assigned_by_user_id`),
  KEY `FKj32ft3ce1vsg7xpffsvixjwei` (`assigned_team_id`),
  KEY `FKlfv6vbvc9hlcsghkhl5aabkaj` (`created_by_user_id`),
  KEY `FK6s1ob9k4ihi75xbxe2w0ylsdh` (`user_id`),
  CONSTRAINT `FK6s1ob9k4ihi75xbxe2w0ylsdh` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
  CONSTRAINT `FKj32ft3ce1vsg7xpffsvixjwei` FOREIGN KEY (`assigned_team_id`) REFERENCES `teams` (`id`),
  CONSTRAINT `FKlfv6vbvc9hlcsghkhl5aabkaj` FOREIGN KEY (`created_by_user_id`) REFERENCES `users` (`id`),
  CONSTRAINT `FKoe33h4u0gc5gsonwrqa5x84kn` FOREIGN KEY (`assigned_by_user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=31 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tasks`
--

LOCK TABLES `tasks` WRITE;
/*!40000 ALTER TABLE `tasks` DISABLE KEYS */;
INSERT INTO `tasks` VALUES (1,NULL,NULL,'2025-10-04 13:51:31.668018','Melhorar experiência do utilizador em dispositivos móveis',NULL,40,'NORMAL','PENDENTE','backend','Melhorar interface mobile','2025-10-06 13:51:31.668082',NULL,3,8,27),(2,15,'2025-10-06 13:51:31.727535','2025-09-21 13:51:31.727452','Implementar sistema de notificações em tempo real','2025-10-15 13:51:31.727486',18,'URGENTE','CONCLUIDA',NULL,'Implementar notificações','2025-10-06 13:51:31.727545',NULL,1,5,18),(3,NULL,NULL,'2025-09-28 13:51:31.748817','Identificar e corrigir problemas críticos reportados pelos utilizadores','2025-11-16 14:51:31.748825',7,'ALTA','PENDENTE',NULL,'Corrigir bugs críticos','2025-10-06 13:51:31.748832',NULL,1,10,11),(4,NULL,NULL,'2025-09-24 13:51:31.769815','Implementar monitorização de sistema e alertas',NULL,6,'URGENTE','PENDENTE','feature','Configurar monitorização','2025-10-06 13:51:31.769843',NULL,2,9,26),(5,NULL,NULL,'2025-09-23 13:51:31.786575','Melhorar experiência do utilizador em dispositivos móveis','2025-11-27 14:51:31.786584',28,'NORMAL','CANCELADA',NULL,'Melhorar interface mobile','2025-10-06 13:51:31.786592',NULL,1,8,20),(6,NULL,NULL,'2025-09-25 13:51:31.807409','Configurar ambiente de produção com alta disponibilidade','2025-10-19 13:51:31.807418',9,'BAIXA','CANCELADA',NULL,'Configurar ambiente produção','2025-10-06 13:51:31.807425',NULL,2,6,30),(7,NULL,NULL,'2025-10-01 13:51:31.827898','Criar sistema de relatórios automáticos para gestão','2025-11-05 14:51:31.827906',35,'NORMAL','CANCELADA','docs','Criar relatórios automáticos','2025-10-06 13:51:31.827913',NULL,1,5,11),(8,17,'2025-10-05 13:51:31.848373','2025-09-15 13:51:31.848357','Implementar endpoints RESTful para comunicação com frontend','2025-10-17 13:51:31.848368',25,'BAIXA','CONCLUIDA','testing','Desenvolver API REST','2025-10-06 13:51:31.848378',NULL,1,8,29),(9,NULL,NULL,'2025-10-01 13:51:31.874245','Configurar ambiente de produção com alta disponibilidade',NULL,34,'URGENTE','CANCELADA',NULL,'Configurar ambiente produção','2025-10-06 13:51:31.874255',NULL,2,4,21),(10,NULL,NULL,'2025-09-27 13:51:31.896611','Realizar formação dos utilizadores finais do sistema',NULL,37,'BAIXA','CANCELADA','testing','Treinar utilizadores finais','2025-10-06 13:51:31.896622',NULL,1,8,28),(11,NULL,NULL,'2025-09-25 13:51:31.930765','Criar sistema de relatórios automáticos para gestão',NULL,11,'NORMAL','CANCELADA',NULL,'Criar relatórios automáticos','2025-10-06 13:51:31.930813',NULL,3,5,28),(12,NULL,NULL,'2025-09-28 13:51:31.953865','Validar se todos os requisitos funcionais estão implementados',NULL,21,'NORMAL','CANCELADA','backend','Validar requisitos funcionais','2025-10-06 13:51:31.953882',NULL,1,7,30),(13,NULL,NULL,'2025-10-05 13:51:31.975132','Desenvolver sistema de autenticação seguro com validação de credenciais','2025-10-25 13:51:31.975138',12,'URGENTE','CANCELADA',NULL,'Implementar login de utilizador','2025-10-06 13:51:31.975146',NULL,1,6,13),(14,NULL,NULL,'2025-09-19 13:51:32.001055','Desenvolver suite completa de testes unitários',NULL,36,'BAIXA','PENDENTE','testing','Criar testes unitários','2025-10-06 13:51:32.001072',NULL,2,5,25),(15,NULL,NULL,'2025-09-15 13:51:32.023016','Configurar sistema de backup automático dos dados','2025-10-14 13:51:32.023025',22,'NORMAL','CANCELADA','frontend','Implementar backup automático','2025-10-06 13:51:32.023032',NULL,2,7,18),(16,NULL,NULL,'2025-09-18 13:51:32.047934','Desenvolver sistema de autenticação seguro com validação de credenciais','2025-12-05 14:51:32.047944',15,'ALTA','PENDENTE','frontend','Implementar login de utilizador','2025-10-06 13:51:32.047950',NULL,1,4,29),(17,NULL,NULL,'2025-10-06 13:51:32.072309','Refatorizar código antigo para melhorar maintibilidade',NULL,30,'NORMAL','CANCELADA',NULL,'Refatorizar código legacy','2025-10-06 13:51:32.072318',NULL,2,10,24),(18,45,'2025-10-06 13:51:32.092405','2025-09-23 13:51:32.092392','Desenvolver suite completa de testes unitários','2025-12-04 14:51:32.092401',38,'BAIXA','CONCLUIDA',NULL,'Criar testes unitários','2025-10-06 13:51:32.092411',NULL,1,4,23),(19,NULL,NULL,'2025-10-06 13:51:32.116861','Melhorar experiência do utilizador em dispositivos móveis','2025-10-10 13:51:32.117021',10,'NORMAL','EM_ANDAMENTO',NULL,'Melhorar interface mobile','2025-10-06 13:51:32.117037',NULL,2,6,20),(20,NULL,NULL,'2025-09-19 13:51:32.145149','Validar se todos os requisitos funcionais estão implementados','2025-10-15 13:51:32.145160',16,'BAIXA','CANCELADA',NULL,'Validar requisitos funcionais','2025-10-06 13:51:32.145168',NULL,3,7,20),(21,NULL,NULL,'2025-10-04 13:51:32.168999','Implementar autenticação de dois fatores para maior segurança','2025-11-11 14:51:32.169006',11,'NORMAL','PENDENTE',NULL,'Implementar autenticação 2FA','2025-10-06 13:51:32.169013',NULL,3,8,29),(22,NULL,NULL,'2025-09-16 13:51:32.193806','Realizar análise completa de segurança do sistema','2025-11-22 14:51:32.193815',5,'NORMAL','PENDENTE',NULL,'Análise de segurança','2025-10-06 13:51:32.193821',NULL,1,6,28),(23,NULL,NULL,'2025-09-19 13:51:32.214699','Implementar endpoints RESTful para comunicação com frontend',NULL,22,'BAIXA','CANCELADA',NULL,'Desenvolver API REST','2025-10-06 13:51:32.214712',NULL,3,8,17),(24,NULL,NULL,'2025-09-18 13:51:32.233081','Refatorizar código antigo para melhorar maintibilidade','2025-10-16 13:51:32.233090',9,'BAIXA','CANCELADA',NULL,'Refatorizar código legacy','2025-10-06 13:51:32.233096',NULL,3,4,22),(25,NULL,NULL,'2025-09-29 13:51:32.253654','Otimizar consultas à base de dados e melhorar tempos de resposta',NULL,31,'NORMAL','CANCELADA','backend','Otimizar performance','2025-10-06 13:51:32.253669',NULL,3,5,26),(26,NULL,NULL,'2025-09-18 13:51:32.274733','Elaborar documentação técnica completa para o sistema',NULL,8,'ALTA','CANCELADA','frontend','Documentação técnica','2025-10-06 13:51:32.274747',NULL,1,5,23),(27,NULL,NULL,'2025-09-21 13:51:32.296201','Implementar endpoints RESTful para comunicação com frontend',NULL,38,'URGENTE','EM_ANDAMENTO','feature','Desenvolver API REST','2025-10-06 13:51:32.296214',NULL,1,6,28),(28,NULL,NULL,'2025-09-15 13:51:32.320679','Desenvolver suite completa de testes unitários','2025-11-02 14:51:32.320688',21,'ALTA','CANCELADA',NULL,'Criar testes unitários','2025-10-06 13:51:32.320777',NULL,3,6,21),(29,NULL,NULL,'2025-09-13 13:51:32.372886','Melhorar experiência do utilizador em dispositivos móveis','2025-11-18 14:51:32.372897',38,'ALTA','PENDENTE',NULL,'Melhorar interface mobile','2025-10-06 13:51:32.372905',NULL,3,7,30),(30,NULL,NULL,'2025-09-28 13:51:32.396685','Implementar sistema de notificações em tempo real','2025-10-15 13:51:32.396693',5,'NORMAL','PENDENTE','testing','Implementar notificações','2025-10-06 13:51:32.396699',NULL,3,4,28);
/*!40000 ALTER TABLE `tasks` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `teams`
--

DROP TABLE IF EXISTS `teams`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `teams` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `active` bit(1) NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `description` text,
  `name` varchar(100) NOT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `manager_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK957exwbsdbxyv2kir751ejid7` (`manager_id`),
  CONSTRAINT `FK957exwbsdbxyv2kir751ejid7` FOREIGN KEY (`manager_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `teams`
--

LOCK TABLES `teams` WRITE;
/*!40000 ALTER TABLE `teams` DISABLE KEYS */;
INSERT INTO `teams` VALUES (1,_binary '','2025-10-06 13:51:31.352754','Responsável pelo desenvolvimento da interface de utilizador','Equipa Frontend','2025-10-06 13:51:31.502609',4),(2,_binary '','2025-10-06 13:51:31.534286','Responsável pelo desenvolvimento do servidor e APIs','Equipa Backend','2025-10-06 13:51:31.582853',5),(3,_binary '','2025-10-06 13:51:31.599240','Responsável pelos testes e controle de qualidade','Equipa Qualidade','2025-10-06 13:51:31.641646',6);
/*!40000 ALTER TABLE `teams` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_profiles`
--

DROP TABLE IF EXISTS `user_profiles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_profiles` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `bio` text,
  `created_at` datetime(6) NOT NULL,
  `date_of_birth` datetime(6) DEFAULT NULL,
  `department` varchar(100) DEFAULT NULL,
  `email_notifications` bit(1) DEFAULT NULL,
  `job_title` varchar(100) DEFAULT NULL,
  `language_preference` varchar(10) DEFAULT NULL,
  `location` varchar(100) DEFAULT NULL,
  `phone_number` varchar(20) DEFAULT NULL,
  `profile_picture_path` varchar(255) DEFAULT NULL,
  `theme_preference` varchar(20) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKe5h89rk3ijvdmaiig4srogdc6` (`user_id`),
  CONSTRAINT `FKjcad5nfve11khsnpwj1mv8frj` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_profiles`
--

LOCK TABLES `user_profiles` WRITE;
/*!40000 ALTER TABLE `user_profiles` DISABLE KEYS */;
/*!40000 ALTER TABLE `user_profiles` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_teams`
--

DROP TABLE IF EXISTS `user_teams`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_teams` (
  `user_id` bigint NOT NULL,
  `team_id` bigint NOT NULL,
  KEY `FK2ndqpo9mm1g72f7hvb9daimrd` (`team_id`),
  KEY `FK5aymw95okwem1l7tmd2owesdh` (`user_id`),
  CONSTRAINT `FK2ndqpo9mm1g72f7hvb9daimrd` FOREIGN KEY (`team_id`) REFERENCES `teams` (`id`),
  CONSTRAINT `FK5aymw95okwem1l7tmd2owesdh` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_teams`
--

LOCK TABLES `user_teams` WRITE;
/*!40000 ALTER TABLE `user_teams` DISABLE KEYS */;
/*!40000 ALTER TABLE `user_teams` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `active` bit(1) DEFAULT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `email` varchar(255) NOT NULL,
  `full_name` varchar(100) DEFAULT NULL,
  `password` varchar(255) NOT NULL,
  `user_role` enum('ADMINISTRADOR','FUNCIONARIO','GERENTE') NOT NULL,
  `username` varchar(50) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK6dotkott2kjsp8vw4d0m25fb7` (`email`),
  UNIQUE KEY `UKr43af9ap4edm43mmtq01oddj6` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=31 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (1,_binary '','2025-10-06 13:42:04.624284','martim.sottomayor@gestordetarefas.pt','Martim Sottomayor','rFrUq2a2pCbD2QHLmFLekg==:Q4slUCoJOfhy2vPwxWtwxOQtJuLLN19AKiler3ONpZ4=','ADMINISTRADOR','martim.sottomayor'),(2,_binary '','2025-10-06 13:42:04.780932','catarina.balsemao@gestordetarefas.pt','Catarina Balsemão','rlTZZYLkjO0XvxBctsa4PQ==:hmfA4o/2tEck7u3H8lKOlt3JNRiSMk2qxxpAX1/gku8=','ADMINISTRADOR','catarina.balsemao'),(3,_binary '','2025-10-06 13:42:04.806623','admin.correia@gestordetarefas.pt','Admin Carlos Correia','WvgOIoK3sUWL4fOveiYSog==:RpDmtxCfllAIo6Wox4oBNaixWZj7Lhw5AJgDXppsXV4=','ADMINISTRADOR','admin.correia'),(4,_binary '','2025-10-06 13:42:04.829804','lucilealmeida@gestordetarefas.pt','Lucile Almeida','nXmSOQwL0Ja2ya5Q8L+poQ==:GwIE5Beq9xbyjJeF9d71qPsDprlVvgEBKQL63Uuuqpo=','GERENTE','lucile.almeida'),(5,_binary '','2025-10-06 13:42:04.856771','bessaribeiro@gestordetarefas.pt','Bessa Ribeiro','nBr2m5Els2jZnMF7HUQqhg==:8Zi4502XqrC6ew1DpmWfc+GeOl4mLU9hWyCgex03MbM=','GERENTE','bessa.ribeiro'),(6,_binary '','2025-10-06 13:42:04.895698','dianabrochado@gestordetarefas.pt','Diana Brochado','hrOfWUJP53gOfBDrllKyVA==:kqb+z3BZscM9rxMx1zX626EYobGmNRjSi9BoBVuoAT4=','GERENTE','diana.brochado'),(7,_binary '','2025-10-06 13:42:04.917953','paulobessa@gestordetarefas.pt','Paulo Bessa','ciNMNRH7kD9sGpJfIcqEsg==:NMVjbn+ffMicZUmpKd+2e8ZbQr33MONiBPLjlZCe3u4=','GERENTE','paulo.bessa'),(8,_binary '','2025-10-06 13:42:04.939939','pedrolopes@gestordetarefas.pt','Pedro Lopes','JFmb/R2kcEpHNh9k3WbpPg==:/892UX0WHIVcRtsZ1XJJnQryoOFfIsOfpvgmGU74uZs=','GERENTE','pedro.lopes'),(9,_binary '','2025-10-06 13:42:04.970030','antonionolasco@gestordetarefas.pt','António Nolasco','oKgJOTICIhYFNhrlQ2qRgg==:Z4fPvYLiMTXfqq7qYKHG+oY8BP8WNZ2e3LOHQ2C7uYY=','GERENTE','antonio.nolasco'),(10,_binary '','2025-10-06 13:42:04.993540','ruigoncalves@gestordetarefas.pt','Rui Gonçalves','0r/1k0oY9YzYmrzNZ7iMgw==:leerZLf8XN2O3O78yEredOfj9bGtfXV9MIbP6hY26jU=','GERENTE','rui.goncalves'),(11,_binary '','2025-10-06 13:42:05.015075','ritaalmeida@gestordetarefas.pt','Rita Almeida','rtO3ArN+iRgwbDoODflqGA==:g5Z8zbqI/8nbujMnL55vX383hoyC+NcK4bwlM3XpiH0=','FUNCIONARIO','rita.almeida'),(12,_binary '','2025-10-06 13:42:05.040971','sandrarocha@gestordetarefas.pt','Sandra Rocha','W9LE6HadyeZiWaYuvuX8tw==:BLyCrQmi5e7PFCvNU6YgAC0kO4AEU+Z198+H+yGnMNM=','FUNCIONARIO','sandra.rocha'),(13,_binary '','2025-10-06 13:42:05.061464','ricardoleal@gestordetarefas.pt','Ricardo Leal','ESxdtADtIlHqsGxnwKjX5Q==:TpGf8nC0WMPb+EI7go60a28MoXZnwJdW5TIgh6hOXeg=','FUNCIONARIO','ricardo.leal'),(14,_binary '','2025-10-06 13:42:05.089280','carlasilva@gestordetarefas.pt','Carla Silva','vA3WSEORNtObba4txuPzoA==:Pddjhk7iT7yor0a6/kXpRbYpw6Egu7fJkkEInvr6fvM=','FUNCIONARIO','carla.silva'),(15,_binary '','2025-10-06 13:42:05.113013','melindaszekely@gestordetarefas.pt','Melinda Szekely','1Y43Le/r/5Towii9uADPXQ==:0TgOwzV6p3VBWLsz0Vql48UkCRpiKs3WJIFgNv4rDEE=','FUNCIONARIO','melinda.szekely'),(16,_binary '','2025-10-06 13:42:05.139193','tatianaalbuquerque@gestordetarefas.pt','Tatiana Albuquerque','/JLCXQMxoxIXrttPkgMSlg==:5KI2zQvtMLgE7GOGFuc/oC+9++z/OqTcrM+/lJPsqKU=','FUNCIONARIO','tatiana.albuquerque'),(17,_binary '','2025-10-06 13:42:05.192358','ritaoliveira@gestordetarefas.pt','Rita Oliveira','JJhk6h/Oob83yNymF2aVCQ==:iByGP4eqlkzcZ5kdLXLod1s49LR9ebPcLQF2QSPoOME=','FUNCIONARIO','rita.oliveira'),(18,_binary '','2025-10-06 13:42:05.213453','anareis@gestordetarefas.pt','Ana Reis','zOF9J++3XqnyUhiEfaehAw==:oSOP7HMkEfZzBol6Oczw4lSGNZbkCyXzesnOmUTjjOU=','FUNCIONARIO','ana.reis'),(19,_binary '','2025-10-06 13:42:05.240846','joaocouto@gestordetarefas.pt','João Couto','00PboP+qRAG69VKMJZNg9A==:RZzyFwddZEcqaRrOD5NY5I/Mn2vuSkor5Guaj93g3gE=','FUNCIONARIO','joao.couto'),(20,_binary '','2025-10-06 13:42:05.265214','inesrodrigues@gestordetarefas.pt','Inês Rodrigues','tbbH0Cler9l7yY5Ow/sA1Q==:GDGWiPYHGoKi5tt6AFGVg6wNOBy0Y2WTTnkZHYrrItM=','FUNCIONARIO','ines.rodrigues'),(21,_binary '','2025-10-06 13:42:05.284486','teresacorreia@gestordetarefas.pt','Teresa Correia','Hmbs/UtNAqZlRHhZokrmbQ==:1/ROiZQmMltdhWaZWFudRmz9XVN9/YMU5P1r0cJuGUU=','FUNCIONARIO','teresa.correia'),(22,_binary '','2025-10-06 13:42:05.303826','vanialourenco@gestordetarefas.pt','Vânia Lourenço','zIFYVU2Peyhbl0AgEA9REw==:3xF7wgRTUVVso31ttMeVx56Xy0TodqNQ4TG9mCwc9Ms=','FUNCIONARIO','vania.lourenco'),(23,_binary '','2025-10-06 13:42:05.319594','ancatusa@gestordetarefas.pt','Anca Tusa','935Zhp384TOx3AQ0wm2B/g==:Bcj9hZ+c1I4igeSSpw56+6q6Z3DiqY+baAg4ifON/V4=','FUNCIONARIO','anca.tusa'),(24,_binary '','2025-10-06 13:42:05.339666','rogeriosilva@gestordetarefas.pt','Rogério Silva','NYVb3HFtSYM/QVAembbuaA==:BULZ6Vvmij4NbQ6vP9hBR4e5Wa1CRJQtzpWPea8/VLg=','FUNCIONARIO','rogerio.silva'),(25,_binary '','2025-10-06 13:42:05.366600','tiagorodrigues@gestordetarefas.pt','Tiago Rodrigues','gOK9YPEtNRSRiIPmeFbx3w==:cL24GhJ3oRuKAoYLDpCDKeCpmatnc40bSFLorTIea7U=','FUNCIONARIO','tiago.rodrigues'),(26,_binary '','2025-10-06 13:42:05.389595','mohammadaldossari@gestordetarefas.pt','Mohammad Al-Dossari','y85HwWjbPeUHjcH8dhfCAA==:5Mnyh9bdSI6ZEr3NMlhlwVwKVlr+9qsn+aMCmvBAn3g=','FUNCIONARIO','mohammad.aldossari'),(27,_binary '','2025-10-06 13:42:05.409714','vijaykumar@gestordetarefas.pt','Vijay Kumar','2tolWmB66MKccG2TjThDCg==:almKAmBGNQwUWjeIitYXUPJS5uXEwn6LHy6LjYjVjG0=','FUNCIONARIO','vijay.kumar'),(28,_binary '','2025-10-06 13:42:05.430890','sanitarahman@gestordetarefas.pt','Sanita Rahman','nnZSEZMQJFCdUVe69DLF4A==:T8jEJV0Fz8XA4Hoe8J0g8b4xfRR1Pt5eF09VX4xxVZM=','FUNCIONARIO','sanita.rahman'),(29,_binary '','2025-10-06 13:42:05.452915','monicalewinsky@gestordetarefas.pt','Mónica Lewinsky','MT4t3ssBobIhYzvnzgZQ4A==:4Ou/X22sEu5oOHyXtI5aSSn0TPRGuU4712AooNLjxZg=','FUNCIONARIO','monica.lewinsky'),(30,_binary '','2025-10-06 13:42:05.476689','cristianaoliveira@gestordetarefas.pt','Cristiana Oliveira','9cGTvQG2KdveSTCqreQQLQ==:ftENgvEj57/LwyLPAr3ZuKfwxWRTEYoGeHN0gIHpTG4=','FUNCIONARIO','cristiana.oliveira');
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-10-06 14:56:20
