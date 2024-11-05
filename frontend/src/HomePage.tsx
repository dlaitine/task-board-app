import { useContext } from 'react';
import { LoginContext } from './context/LoginContext';
import { Box, Divider, Stack, Typography } from '@mui/material';
import Grid from '@mui/material/Grid2';
import { NewBoardForm } from './board/form/NewBoardForm';
import { PublicBoardList } from './board/PublicBoardList';
import { PrivateBoardList } from './board/PrivateBoardList';

export const HomePage = () => {

  const { username, } = useContext(LoginContext);

  return (
    <>
      <Stack padding={4} display='flex' justifyContent='center' alignItems='center'>
        <Typography variant='h4'>Welcome, {username}!</Typography>
        <Typography variant='body1'>
          Here you can access public boards and create new ones.
        </Typography>
        <Box display='flex' justifyContent='center' alignItems='center' minHeight='40vh'>
          <Grid container spacing={2} columns={11} sx={{ flexDirection: { xs: "column", md: "row", lg: 'row', } }}>
            <Grid size={{ xs: 10, md: 5, lg: 5 }}>
              <PublicBoardList />
              <PrivateBoardList />
            </Grid>
            <Divider orientation="vertical" flexItem sx={{ mr: "-1px" }} />
            <Grid size={{ xs: 10, md: 5, lg: 5, }}>
              <NewBoardForm />
            </Grid>
          </Grid>
        </Box>
      </Stack>
      
    </>
  );
};
