import { IconButton, ListItem, ListItemButton, ListItemText } from "@mui/material";
import NavigateNextIcon from '@mui/icons-material/NavigateNext';
import { Board } from "./board";
import { useNavigate } from "react-router-dom";

interface PublicBoardListItemProps {
  board: Board;
};

export const PublicBoardListItem = ({ board, }: PublicBoardListItemProps) => {
  const navigate = useNavigate();

  return (
    <ListItem
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
};