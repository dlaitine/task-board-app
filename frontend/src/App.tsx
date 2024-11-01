import { useContext } from 'react';
import { LoginContext } from './task/context/LoginContext';
import { LoginForm } from './task/form/LoginForm';
import { Route, Routes } from 'react-router-dom';
import { BoardPage } from './task/BoardPage';
import { HomePage } from './task/HomePage';
import { ResponsiveAppBar } from './task/ResponsiveAppBar';
import { SocketErrorPopUp } from './task/error/SocketErrorPopUp';

const App = () => {
  const { username, }  = useContext(LoginContext);

  if (!username) {
    return <LoginForm />;
  }

  return (
    <>
      <ResponsiveAppBar />
      <SocketErrorPopUp />
      <Routes>
        <Route path="/" element={<HomePage />}></Route>
        <Route path="/:boardId" element={<BoardPage />}></Route>
      </Routes>
    </>
  );
};

export default App;