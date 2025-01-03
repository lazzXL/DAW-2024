# Passos para arranque do sistema

## Realizar clone do repositório.

# Inicialização do backend:

## 1. Abrir o projeto no intelliJ IDEA.

## 2. Adicionar JDK 21 ao projeto em File -> Project Structure -> Project -> Project SDK -> Add SDK -> JDK.

## 3. Editar o Run Configuration em Run with Parameters:

### 3.1. Adicionar variável de ambiente: DB_URL=jdbc:postgresql://localhost:5431/postgres?user=postgres&password=1212

### 3.2. Selecionar JDK 21 em JRE.

## 4. Executar o comando ./gradlew runDocker (no diretório code/jvm)

## 5. Correr o projeto.

# Inicialização do frontend:

## 1. Executar na linha de comandos:

### 1.1. npm install

### 1.2. npm start

Utilizadores para teste: \
Nome: Marco, Senha: Marco1999\
Nome: Bobby, Senha: Bob1999

