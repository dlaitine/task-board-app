import React, { createContext, useState } from 'react';
import { useSubscription } from 'react-stomp-hooks';

interface NotificationContextProps {
  message: string;
  setMessage: (message: string) => void;
  severity: 'success' | 'error';
  setSeverity: (severity: 'success' | 'error') => void;
}

export const NotificationContext = createContext<NotificationContextProps>({
  message: '',
  setMessage: () => {},
  severity: 'success',
  setSeverity: () => {},
});

export const NotificationProvider = ({
  children,
}: {
  children: React.ReactNode;
}) => {
  const [message, setMessage] = useState<string>('');
  const [severity, setSeverity] = useState<'success' | 'error'>('success');

  // Subscribe to STOMP errors
  useSubscription('/user/queue/error', (error) => {
    const { error_msg }: { error_msg: string } = JSON.parse(error.body);
    setMessage(error_msg);
    setSeverity('error');
  });

  const notificationContextValue: NotificationContextProps = {
    message,
    setMessage: setMessage,
    severity: severity,
    setSeverity: setSeverity,
  };

  return (
    <NotificationContext.Provider value={notificationContextValue}>
      {children}
    </NotificationContext.Provider>
  );
};
