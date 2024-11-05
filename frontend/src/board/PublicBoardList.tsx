import { Box, List, Typography } from "@mui/material";
import LoadingSpinner from "../common/LoadingSpinner";
import { useContext, useEffect, useState } from "react";
import { Board } from "./board";
import { NotificationContext } from "../context/NotificationContext";
import { baseUrl } from "../common/constants";
import { PublicBoardListItem } from "./PublicBoardListItem";

export const PublicBoardList = () => {
  const [ boards, setBoards, ] = useState<Board[]>([]);

  const [ loading, setLoading, ] = useState<boolean>(false);

  const { setMessage, setSeverity, } = useContext(NotificationContext);

  useEffect(() => {
    const controller = new AbortController();
    const signal = controller.signal;

    setLoading(true);
    fetch(`${baseUrl}/boards`, { signal, })
      .then((response) => {
        if (!response.ok) {
          throw Error(`Board fetching failed with status: ${response.statusText}`)
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

    return () => {
      controller.abort(); // Cancel the fetch request when the component is unmounted
    };
  // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  return (
    <Box padding={2}>
      <Typography variant='h4'>Public boards</Typography>
      {
        loading ?
        <LoadingSpinner /> :
        <List sx={{ width: '100%', maxWidth: 360, maxHeight: 360, overflow: 'auto', bgcolor: 'background.paper', }}>
          {boards.map((board) => {
            return <PublicBoardListItem key={board.id} board={board} />
          })}
        </List>
      }
    </Box>
  );
};