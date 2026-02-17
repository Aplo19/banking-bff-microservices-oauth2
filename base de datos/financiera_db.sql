-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Feb 05, 2026 at 08:14 PM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `financiera_db`
--

-- --------------------------------------------------------

--
-- Table structure for table `auth_users`
--

CREATE TABLE `auth_users` (
  `id` bigint(20) NOT NULL,
  `username` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  `email` varchar(255) DEFAULT NULL,
  `first_name` varchar(255) DEFAULT NULL,
  `last_name` varchar(255) DEFAULT NULL,
  `enabled` tinyint(1) DEFAULT 1,
  `account_non_expired` tinyint(1) DEFAULT 1,
  `account_non_locked` tinyint(1) DEFAULT 1,
  `credentials_non_expired` tinyint(1) DEFAULT 1,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `updated_at` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `auth_users`
--

INSERT INTO `auth_users` (`id`, `username`, `password`, `email`, `first_name`, `last_name`, `enabled`, `account_non_expired`, `account_non_locked`, `credentials_non_expired`, `created_at`, `updated_at`) VALUES
(1, 'admin', '{bcrypt}$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iK6oycAEQbsPJ/D11kE4wT9EFltQ', 'admin@banco.com', 'Admin', 'Sistema', 1, 1, 1, 1, '2026-02-04 16:55:34', '2026-02-04 16:55:34'),
(2, 'cliente', '{bcrypt}$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iK6oycAEQbsPJ/D11kE4wT9EFltQ', 'cliente@banco.com', 'Juan', 'Pérez', 1, 1, 1, 1, '2026-02-04 16:55:34', '2026-02-04 16:55:34'),
(3, 'analista', '{bcrypt}$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iK6oycAEQbsPJ/D11kE4wT9EFltQ', 'analista@banco.com', 'María', 'Gómez', 1, 1, 1, 1, '2026-02-04 16:55:34', '2026-02-04 16:55:34');

-- --------------------------------------------------------

--
-- Table structure for table `clientes`
--

CREATE TABLE `clientes` (
  `id` bigint(20) NOT NULL,
  `codigo_unico` varchar(255) NOT NULL,
  `nombres` varchar(255) NOT NULL,
  `apellidos` varchar(255) NOT NULL,
  `tipo_documento` varchar(255) NOT NULL,
  `numero_documento` varchar(255) NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `clientes`
--

INSERT INTO `clientes` (`id`, `codigo_unico`, `nombres`, `apellidos`, `tipo_documento`, `numero_documento`, `created_at`) VALUES
(2, 'CLI002', 'María', 'Gómez', 'CE', '87654321', '2026-02-04 05:39:51'),
(3, 'CLI001', 'Juan', 'Perez', 'DNI', '12345678', '2026-02-05 17:57:49');

-- --------------------------------------------------------

--
-- Table structure for table `productos_financieros`
--

CREATE TABLE `productos_financieros` (
  `id` bigint(20) NOT NULL,
  `cliente_id` bigint(20) NOT NULL,
  `codigo_unico_cliente` varchar(255) NOT NULL,
  `tipo_producto` varchar(255) NOT NULL,
  `nombre_producto` varchar(255) NOT NULL,
  `saldo` decimal(15,2) DEFAULT 0.00,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `productos_financieros`
--

INSERT INTO `productos_financieros` (`id`, `cliente_id`, `codigo_unico_cliente`, `tipo_producto`, `nombre_producto`, `saldo`, `created_at`) VALUES
(3, 2, 'CLI002', 'CUENTA_CORRIENTE', 'Corriente Empresarial', 15000.00, '2026-02-04 05:18:15'),
(4, 3, 'CLI001', 'CUENTA_AHORROS', 'Ahorro Plus', 1000.00, '2026-02-05 17:57:49'),
(5, 3, 'CLI001', 'TARJETA_CREDITO', 'Visa Gold', 500.00, '2026-02-05 17:57:49');

-- --------------------------------------------------------

--
-- Table structure for table `user_roles`
--

CREATE TABLE `user_roles` (
  `id` bigint(20) NOT NULL,
  `user_id` bigint(20) NOT NULL,
  `role` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `user_roles`
--

INSERT INTO `user_roles` (`id`, `user_id`, `role`) VALUES
(1, 1, 'ROLE_ADMIN'),
(2, 1, 'ROLE_USER'),
(3, 2, 'ROLE_CLIENTE'),
(4, 3, 'ROLE_ANALISTA');

-- --------------------------------------------------------

--
-- Table structure for table `usuarios`
--

CREATE TABLE `usuarios` (
  `id` int(11) NOT NULL,
  `username` varchar(50) NOT NULL,
  `password` varchar(255) NOT NULL,
  `enabled` tinyint(1) DEFAULT 1,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `usuarios`
--

INSERT INTO `usuarios` (`id`, `username`, `password`, `enabled`, `created_at`) VALUES
(1, 'admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iK6oycAEQbsPJ/D11kIyBVqeFQM.', 1, '2026-02-04 05:18:15');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `auth_users`
--
ALTER TABLE `auth_users`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `username` (`username`);

--
-- Indexes for table `clientes`
--
ALTER TABLE `clientes`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `codigo_unico` (`codigo_unico`),
  ADD UNIQUE KEY `numero_documento` (`numero_documento`),
  ADD KEY `idx_codigo_unico` (`codigo_unico`);

--
-- Indexes for table `productos_financieros`
--
ALTER TABLE `productos_financieros`
  ADD PRIMARY KEY (`id`),
  ADD KEY `idx_codigo_unico` (`codigo_unico_cliente`),
  ADD KEY `cliente_id` (`cliente_id`);

--
-- Indexes for table `user_roles`
--
ALTER TABLE `user_roles`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `unique_user_role` (`user_id`,`role`);

--
-- Indexes for table `usuarios`
--
ALTER TABLE `usuarios`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `username` (`username`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `auth_users`
--
ALTER TABLE `auth_users`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT for table `clientes`
--
ALTER TABLE `clientes`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT for table `productos_financieros`
--
ALTER TABLE `productos_financieros`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT for table `user_roles`
--
ALTER TABLE `user_roles`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=8;

--
-- AUTO_INCREMENT for table `usuarios`
--
ALTER TABLE `usuarios`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `user_roles`
--
ALTER TABLE `user_roles`
  ADD CONSTRAINT `user_roles_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `auth_users` (`id`) ON DELETE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
