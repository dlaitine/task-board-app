import { Box, IconButton, List, ListItem, ListItemButton, ListItemText, Typography } from "@mui/material";
import NavigateNextIcon from '@mui/icons-material/NavigateNext';
import LoadingSpinner from "../LoadingSpinner";
import { useContext, useEffect, useState } from "react";
import { Board } from "./board";
import { useNavigate } from "react-router-dom";
import { NotificationContext } from "../context/NotificationContext";
import { baseUrl } from "../constants";

export const PublicBoardList = () => {
  const navigate = useNavigate();

  const [ boards, setBoards, ] = useState<Board[]>([]);

  const [ loading, setLoading, ] = useState<boolean>(false);

  const { setMessage, setSeverity, } = useContext(NotificationContext);

  useEffect(() => {
    const controller = new AbortController();
    setLoading(true);
    fetch(`${baseUrl}/boards`)
      .then((response) => {
        if (!response.ok) {
          throw Error('Board fetching failed')
        }
        return response.json();
      })
      .then((data) => {
        setBoards(data);
      })
      .finally(() => {
        setLoading(false);
      })
      .catch((error) => {
        if (!controller.signal.aborted) {
          setSeverity('error');
          setMessage(error.message);
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
            return (
              <ListItem
                key={board.id}
                disablePadding
              >
                <ListItemButton  dense onClick={() => navigate("/" + board.id)}>
                  <ListItemText id={board.id} primary={board.name} />
                  <IconButton disableRipple edge="end" aria-label="goto" sx={{ padding: 0, }}>
                    <NavigateNextIcon />
                  </IconButton>
                </ListItemButton>
              </ListItem>
            );
          })}
        </List>
      }
    </Box>
  );
};