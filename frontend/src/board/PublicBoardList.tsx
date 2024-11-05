import { Box, IconButton, List, Tooltip, Typography } from '@mui/material';
import LoadingSpinner from '../common/LoadingSpinner';
import { PublicBoardListItem } from './PublicBoardListItem';
import { usePublicBoards } from './hook/boardApiHooks';
import RefreshIcon from '@mui/icons-material/Refresh';

export const PublicBoardList = () => {
  const { loading, boards, refetch } = usePublicBoards();

  return (
    <Box padding={2}>
      <Typography variant="h4">
        Public boards
        <Tooltip title="Refresh">
          <IconButton aria-label="delete" size="medium" onClick={refetch}>
            <RefreshIcon fontSize="inherit" />
          </IconButton>
        </Tooltip>
      </Typography>
      {loading ? (
        <LoadingSpinner />
      ) : (
        <List
          sx={{
            width: '100%',
            maxWidth: 360,
            maxHeight: 360,
            overflow: 'auto',
            bgcolor: 'background.paper',
          }}
        >
          {boards.map((board) => {
            return <PublicBoardListItem key={board.id} board={board} />;
          })}
        </List>
      )}
    </Box>
  );
};
