import { useContext } from 'react';
import { LoginContext } from './context/LoginContext';
import { Route, Routes } from 'react-router-dom';
import { BoardPage } from './board/BoardPage';
import { ResponsiveAppBar } from './common/ResponsiveAppBar';
import { ErrorPopUp } from './common/NotificationPopUp';
import { NotFoundPage } from './error/NotFoundPage';
import { HomePage } from './HomePage';
import { LoginForm } from './common/LoginForm';
import { BoardProvider } from './context/BoardContext';

const App = () => {
  const { username, } = useContext(LoginContext);

  if (!username) {
    return <LoginForm />;
  }

  return (
    <>
      <ResponsiveAppBar />
      <ErrorPopUp />
      <Routes>
        <Route path='/' element={<HomePage />}></Route>
        <Route path='/:boardId' element={
          <BoardProvider>
            <BoardPage />
          </BoardProvider>
        }>
        </Route>
        <Route path='/not-found' element={<NotFoundPage />}></Route>
      </Routes>
    </>
  );
};

export default App;