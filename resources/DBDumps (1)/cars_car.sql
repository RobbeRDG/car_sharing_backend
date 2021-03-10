-- MySQL dump 10.13  Distrib 8.0.23, for Win64 (x86_64)
--
-- Host: localhost    Database: cars
-- ------------------------------------------------------
-- Server version	8.0.23

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `car`
--

DROP TABLE IF EXISTS `car`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `car` (
  `car_id` bigint NOT NULL,
  `color` varchar(255) NOT NULL,
  `in_maintenance` bit(1) NOT NULL,
  `in_use` bit(1) NOT NULL,
  `location` geometry NOT NULL,
  `manufacturer` varchar(255) NOT NULL,
  `model` varchar(255) NOT NULL,
  `number_of_seats` int NOT NULL,
  `number_plate` varchar(255) NOT NULL,
  `remaining_fuel_in_kilometers` int NOT NULL,
  `version` bigint NOT NULL,
  `year_of_manufacture` varchar(255) NOT NULL,
  `reservation_id` bigint DEFAULT NULL,
  PRIMARY KEY (`car_id`),
  UNIQUE KEY `UK_1wy3rm2ysgt7gevwji4v205ri` (`number_plate`),
  KEY `FKbj3ft9adr73g9avdqau3qa4ku` (`reservation_id`),
  CONSTRAINT `FKbj3ft9adr73g9avdqau3qa4ku` FOREIGN KEY (`reservation_id`) REFERENCES `reservation` (`reservation_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `car`
--

LOCK TABLES `car` WRITE;
/*!40000 ALTER TABLE `car` DISABLE KEYS */;
INSERT INTO `car` VALUES (1,'#ffffff',_binary '\0',_binary '\0',_binary '\Ê\0\0\0\0\0Qköwúb\r@¸5Y£ÇI@','Peugeot','208',4,'1-KVF-126',441,0,'2013',NULL),(2,'#000000',_binary '\0',_binary '\0',_binary '\Ê\0\0\0\0\0\Îˇ\Ê\À\r@πn¿ÉI@','Peugeot','208',4,'1-HKS-323',213,0,'2013',NULL),(3,'#cc1010',_binary '\0',_binary '\0',_binary '\Ê\0\0\0\0\0ÑûÕ™œï@/iå\÷QuI@','Peugeot','208',4,'1-JJL-421',324,5,'2013',14),(4,'#ffffff',_binary '\0',_binary '\0',_binary '\Ê\0\0\0\0\0.\ÁR\\U∂\r@n˙≥)äI@','Opel','Corsa',4,'1-LMP-478',132,0,'2018',NULL),(5,'#000000',_binary '\0',_binary '\0',_binary '\Ê\0\0\0\0\0\Êë?xn\r@[ôK˝àI@','Opel','Corsa',4,'1-KMP-333',601,0,'2018',NULL),(6,'#cc1010',_binary '\0',_binary '\0',_binary '\Ê\0\0\0\0\0\≈=ñ>t@\Ÿ\Œ˜SáI@','Opel','Corsa',4,'1-TLS-456',81,0,'2018',NULL),(7,'#ffffff',_binary '\0',_binary '\0',_binary '\Ê\0\0\0\0\0\Z\›A\ÏL!@jMÛéSÑI@','Opel','Corsa',4,'1-APJ-321',423,0,'2018',NULL),(8,'#1026b5',_binary '\0',_binary '\0',_binary '\Ê\0\0\0\0\0>\–\nY\›\r@êIF\Œ¬ÜI@','Volkswagen','Polo',4,'1-JCV-738',168,0,'2020',NULL),(9,'#ffffff',_binary '\0',_binary '\0',_binary '\Ê\0\0\0\0\0Åx]ø\‡\r@º\"¯\ﬂJÜI@','Volkswagen','Polo',4,'1-DCE-568',523,0,'2020',NULL),(10,'#1026b5',_binary '\0',_binary '\0',_binary '\Ê\0\0\0\0\0có®\ﬁ\Zò\r@\ﬁqäé\‰äI@','Volkswagen','Polo',4,'1-PLA-911',580,0,'2020',NULL);
/*!40000 ALTER TABLE `car` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2021-03-10  9:49:56
