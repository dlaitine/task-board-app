import { useContext, useEffect, useState } from 'react';
import { Board } from '../board';
import { NotificationContext } from '../../context/NotificationContext';
import { baseUrl } from '../../common/constants';

export const usePublicBoards = () => {
  const [boards, setBoards] = useState<Board[]>([]);
  const [loading, setLoading] = useState<boolean>(false);

  const { setMessage, setSeverity } = useContext(NotificationContext);

  const [shouldRefetch, refetch] = useState({});

  const fetchData = (controller: AbortController) => {
    const signal = controller.signal;

    setLoading(true);
    fetch(`${baseUrl}/boards`, { signal })
      .then((response) => {
        if (!response.ok) {
          throw Error(
            `Board fetching failed with status: ${response.statusText}`,
          );
        }
        return response.json();
      })
      .then((data) => {
        setBoards(data);
      })
      .finally(() => {
        if (!controller.signal.aborted) {
          setLoading(false);
        }
      })
      .catch((error) => {
        if (!controller.signal.aborted) {
          setSeverity('error');
          setMessage(`Error when fetching public boards: ${error.message}`);
        }
      });
  };

  useEffect(() => {
    const controller = new AbortController();
    fetchData(controller);

    return () => {
      controller.abort(); // Cancel the fetch request when the component is unmounted
    };
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [shouldRefetch]);

  return {
    loading,
    boards,
    refetch: () => refetch({}),
  };
};
