# Loja Automática API (UESB)

[![Java](https://img.shields.io/badge/Java-21-blue.svg)](https://www.java.com) [![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-brightgreen.svg)](https://spring.io/projects/spring-boot) [![Docker](https://img.shields.io/badge/Docker-gray.svg?logo=docker)](https://www.docker.com/) [![PostgreSQL](https://img.shields.io/badge/PostgreSQL-gray.svg?logo=postgresql)](https://www.postgresql.org/) [![Elasticsearch](https://img.shields.io/badge/Elasticsearch-gray.svg?logo=elasticsearch)](https://www.elastic.co/)

API RESTful para um sistema de e-commerce, desenvolvida como parte do projeto da Loja Automática do Pet Computação na UESB. O projeto foi construído com foco em uma arquitetura moderna, robusta e escalável, utilizando as melhores práticas do ecossistema Spring.

## Status do Projeto

**Núcleo de Funcionalidades do Backend: Concluído ✅**
(Próximos passos incluem a implementação de testes automatizados e a orquestração do ambiente com Docker Compose).

---

## Funcionalidades Implementadas

O backend cobre todo o fluxo de negócio essencial de um e-commerce:

* ✅ **Autenticação & Autorização:** Sistema completo com JWT e perfis de acesso (`USER` / `ADMIN`), utilizando Spring Security.
* ✅ **Gerenciamento de Usuários:** Cadastro, login, e endpoints seguros para que o usuário gerencie seu próprio perfil.
* ✅ **Catálogo de Produtos:** CRUD completo de produtos, com endpoints de criação/edição/deleção protegidos para administradores.
* ✅ **Busca Otimizada:** Endpoint de busca de produtos paginado e com filtros de texto, utilizando **Elasticsearch** para alta performance.
* ✅ **Fluxo de Carrinho de Compras:** Sistema completo para gerenciamento de carrinho de compras ativo, com endpoints para adicionar, remover e atualizar a quantidade de itens.
* ✅ **Checkout e Pagamento:** Integração com o gateway de pagamento **Stripe** para iniciar o processo de pagamento.
* ✅ **Confirmação Automática de Pedidos:** Endpoint de **Webhook** para receber notificações do Stripe, confirmar o pagamento, atualizar o status do pedido e dar baixa no estoque.
* ✅ **Gestão de Estoque:** Lógica transacional e segura para garantir a consistência do inventário.
* ✅ **Histórico de Compras:** Endpoint paginado para que o usuário possa consultar suas compras finalizadas.
* ✅ **Documentação de API:** Documentação completa e interativa com Swagger/OpenAPI.

---

## Tecnologias Utilizadas

* **Backend:** Java 21, Spring Boot 3, Spring Security, Spring Data JPA, Spring Data Elasticsearch
* **Banco de Dados Primário:** PostgreSQL
* **Motor de Busca:** Elasticsearch
* **Pagamentos:** Stripe API
* **Ambiente e DevOps:** Docker, Docker Compose
* **Migrações de Banco:** Flyway
* **Mapeamento de Objetos:** MapStruct
* **Testes (em desenvolvimento):** JUnit 5, Mockito

---

## Como Rodar o Projeto (Ambiente de Desenvolvimento)

#### 1. Pré-requisitos
* Git
* Java JDK 21+
* Maven 3.8+
* Docker e Docker Compose

#### 2. Clonar o Repositório
```bash
git clone [https://github.com/FrancoBorba/loja-automatica.git](https://github.com/FrancoBorba/loja-automatica.git)
cd loja-automatica
```

#### 3. Configuração de Ambiente (Variáveis Secretas)
Este projeto usa um arquivo de configuração local para armazenar senhas e chaves de API, garantindo que nenhum segredo seja enviado para o repositório Git.

1.  **Crie seu arquivo de configuração local:**
    Na pasta `src/main/resources/`, crie uma cópia do arquivo de exemplo `application-local.yml.example`. Você pode renomear a cópia para `application-local.yml`.

2.  **Preencha seus segredos:**
    Abra o novo arquivo `application-local.yml` e preencha os valores com suas próprias credenciais (senha do banco, chaves do Stripe, etc.). O arquivo `application.yml` principal já está configurado para carregar este arquivo automaticamente.

#### 4. Iniciar os Serviços com Docker Compose
Este comando irá iniciar os containers do PostgreSQL e do Elasticsearch.
```bash
docker-compose up -d
```
*(Aguarde um ou dois minutos para que os serviços iniciem completamente).*

#### 5. Executar a Aplicação Spring Boot
Você pode executar pela sua IDE ou pelo terminal com o Maven:
```bash
mvn spring-boot:run
```
A aplicação estará rodando em `http://localhost:8080`.

---

## Documentação da API (Swagger)
Com a aplicação rodando, a documentação interativa da API está disponível em:
* [**http://localhost:8080/swagger-ui.html**](http://localhost:8080/swagger-ui.html)

Você pode usar o Swagger para testar todos os endpoints. Lembre-se de usar o fluxo de `/api/auth/register` e `/api/auth/login` para obter um token JWT e autorizar suas requisições no botão "Authorize".