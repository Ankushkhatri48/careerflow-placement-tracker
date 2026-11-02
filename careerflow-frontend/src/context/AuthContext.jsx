import React, { createContext, useContext, useState, useEffect } from 'react';
import api from '../api/client';

const AuthContext = createContext(null);

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const token = localStorage.getItem('careerflow_token');
    const savedUser = localStorage.getItem('careerflow_user');
    if (token && savedUser) {
      try {
        setUser(JSON.parse(savedUser));
      } catch (e) {
        localStorage.removeItem('careerflow_token');
        localStorage.removeItem('careerflow_user');
      }
    }
    setLoading(false);
  }, []);

  const login = async (email, password) => {
    const res = await api.post('/auth/login', { email, password });
    const { accessToken, refreshToken, user: userData } = res.data;
    localStorage.setItem('careerflow_token', accessToken);
    localStorage.setItem('careerflow_refresh_token', refreshToken);
    localStorage.setItem('careerflow_user', JSON.stringify(userData));
    setUser(userData);
    return userData;
  };

  const register = async (fullName, email, password) => {
    await api.post('/auth/register', { fullName, email, password });
    return login(email, password);
  };

  const logout = () => {
    localStorage.removeItem('careerflow_token');
    localStorage.removeItem('careerflow_refresh_token');
    localStorage.removeItem('careerflow_user');
    setUser(null);
  };

  return (
    <AuthContext.Provider value={{ user, loading, login, register, logout }}>
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => useContext(AuthContext);
