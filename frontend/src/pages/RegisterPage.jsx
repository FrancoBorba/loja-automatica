// Em frontend/src/pages/RegisterPage.jsx

import React, { useState } from 'react';
import { Link } from 'react-router-dom';
import './RegisterPage.css';

// Importar sua logo principal aqui (IconePeti.png)
import IconePeti from '../assets/IconePeti.png';

// Importar os ícones sociais
import IconeGoogleGmail from '../assets/IconeGoogleGmail.png';
import IconeFacebook from '../assets/IconeFacebook.png';
import IconeApple from '../assets/IconeApple.png';


export default function RegisterPage() {
  const [name, setName] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');

  const handleSubmit = (event) => {
    event.preventDefault();
    console.log("Tentando cadastrar:", { name, email, password });
    // TODO: Chamar o serviço da API para registrar
  };

  return (
    <div className="register-page-outer-container"> {/* Container externo para o gradiente de fundo */}
      <div className="register-card-wrapper"> {/* Wrapper para o card principal */}
        <div className="register-form-panel"> {/* Painel esquerdo do formulário */}
          <div className="register-header-section">
            <img src={IconePeti} alt="Logo da Loja" className="register-logo" />
            <h2 className="register-main-title">Faça seu cadastro</h2>
            <p className="register-subtitle">Comece agora e tenha acesso a todos os recursos da lojinha PETI UESB</p>
          </div>

          <form onSubmit={handleSubmit} className="register-form">
            <div className="form-group">
              <label htmlFor="name">Nome</label>
              <input
                type="text"
                id="name"
                placeholder="Seu nome"
                value={name}
                onChange={e => setName(e.target.value)}
                className="form-input"
              />
            </div>
            
            <div className="form-group">
              <label htmlFor="email">Email</label>
              <input
                type="email"
                id="email"
                placeholder="Seu email"
                value={email}
                onChange={e => setEmail(e.target.value)}
                className="form-input"
              />
            </div>
            
            <div className="form-group">
              <label htmlFor="password">Senha</label>
              <div className="password-input-wrapper">
                <input
                  type="password"
                  id="password"
                  placeholder="Crie uma senha"
                  value={password}
                  onChange={e => setPassword(e.target.value)}
                  className="form-input"
                />
                {/* Aqui vai o ícone da seta/olho para mostrar/esconder senha, se você quiser. */}
                {/* Por enquanto, deixei como um placeholder de texto */}
                <span className="password-toggle-icon">▼</span> 
              </div>
            </div>
            
            <button type="submit" className="register-button">Realizar Cadastro</button>

            <div className="register-divider">ou</div>

            <div className="social-login">
              <button type="button" className="social-button">
                  <img src={IconeGoogleGmail} alt="Login com Google" className="social-icon" />
              </button>
              <button type="button" className="social-button">
                  <img src={IconeFacebook} alt="Login com Facebook" className="social-icon" />
              </button>
              <button type="button" className="social-button">
                  <img src={IconeApple} alt="Login com Apple" className="social-icon" />
              </button>
            </div>
          </form>
        </div>

            <div className="register-info-panel">
          
          {/* Div para o conteúdo principal (texto) */}
          <div className="info-panel-content">
            <h3 className="info-panel-text">Explore, participe e aproveite todas as vantagens que preparamos para você.</h3>
          </div>

          {/* Div separada para a chamada de login no rodapé do painel */}
          <div className="info-panel-login-cta">
            <span>Já possui uma conta?</span>
            <Link to="/login" className="info-panel-login-button">Login</Link>
          </div>

        </div>
      </div>
    </div>
  );
}