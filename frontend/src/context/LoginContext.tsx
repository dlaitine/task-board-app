import React, { createContext, useState } from 'react';
import { useNavigate } from 'react-router-dom';

interface LoginContextProps {
  username: string | null;
  login: (user: string) => void;
  logout: () => void;
}

export const LoginContext = createContext<LoginContextProps>({
  username: null,
  login: () => {},
  logout: () => {}
});

export const LoginProvider = ({ children } : { children: React.ReactNode }) => {
  const navigate = useNavigate();

  const [username, setUsername] = useState<string | null>(localStorage.getItem('username') || null);

  const loginHandler = (username: string) => {
    localStorage.setItem('username', username);
    setUsername(username);
  };

  const logoutHandler = () => {
    localStorage.removeItem('username');
    setUsername(null);
    navigate('/');
  };

  const loginContextValue: LoginContextProps = {
    username,
    login: loginHandler,
    logout: logoutHandler
  };

  return (
    <LoginContext.Provider value={loginContextValue}>
      {children}
    </LoginContext.Provider>
  );
};