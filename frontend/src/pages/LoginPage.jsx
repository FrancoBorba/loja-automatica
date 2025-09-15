import React, { useState } from 'react';
import './LoginPage.css'; 
import authService from '../services/authService';
import { useNavigate, Link } from 'react-router-dom';


 import IconePeti from '../assets/IconePeti.png'; 
import IconeGoogleGmail from '../assets/IconeGoogleGmail.png'; 
import IconeFacebook from '../assets/IconeFacebook.png'; 
import IconeApple from '../assets/IconeApple.png'; 

export default function LoginPage() {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');

  const [error, setError] = useState('');
  const navigate = useNavigate(); // PASSO 2: Pega a função de navegação

  const handleSubmit = async (event) => {
    event.preventDefault();
    setError(''); // Limpa erros antigos

    try {
      // 3. Chama o serviço de login e espera pela resposta
      const response = await authService.login(email, password);
      
      // 4. Se o login for bem-sucedido
      console.log('Login bem-sucedido! Token:', response.token);

       if (response.token) {
        localStorage.setItem('authToken', response.token);
        navigate('/');
      }

      // TODO: Salvar o token e redirecionar o usuário
      
    } catch (err) {
 
      console.error('Falha no login:', err);
      setError('E-mail ou senha inválidos. Tente novamente.');
    }
  };

  return (
    <div className="login-page-container">
      <div className="login-card">
        <div className="login-header">
           
            <img src={IconePeti} alt="Logo da Loja" className="login-logo" />
            <h2 className="login-title">Login</h2>
        </div>

        <form onSubmit={handleSubmit} className="login-form">
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
            <input
              type="password"
              id="password"
              placeholder="Crie uma senha"
              value={password}
              onChange={e => setPassword(e.target.value)}
              className="form-input"
            />
          </div>

          <div className="login-links">
          <Link to="/register" className="link">Fazer cadastro</Link>
          <Link to="/forgot-password" className="link">Esqueci a senha</Link>
          </div>
          
          <button type="submit" className="login-button">Entrar</button>

          <div className="divider">ou</div>

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
    </div>
  );
}