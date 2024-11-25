import { useContext } from 'react';
import { LoginContext } from './context/LoginContext';
import { Box, Divider, Stack, Typography } from '@mui/material';
import Grid from '@mui/material/Grid2';
import { NewBoardForm } from './board/form/NewBoardForm';
import { PublicBoardList } from './board/PublicBoardList';
import { PrivateBoardList } from './board/PrivateBoardList';

export const HomePage = () => {
  const { username } = useContext(LoginContext);

  return (
    <>
      <Stack
        padding={4}
        display="flex"
        justifyContent="center"
        alignItems="center"
      >
        <Typography variant="h4">Welcome, {username}!</Typography>
        <Typography variant="body1">
          Here you can access public boards and create new ones.
        </Typography>
        <Box
          paddingTop={2}
          display="flex"
          justifyContent="center"
          alignItems="top"
          minHeight="40vh"
        >
          <Grid
            container
            spacing={2}
            columns={{ xs: 10, md: 11, lg: 11 }}
            sx={{ flexDirection: { xs: 'column', md: 'row', lg: 'row' } }}
          >
            <Grid size={{ xs: 10, md: 5, lg: 5 }}>
              <PublicBoardList />
              <PrivateBoardList />
            </Grid>
            <Grid
              size={{ xs: 0, md: 1, lg: 1 }}
              container
              justifyContent="center"
            >
              <Divider
                orientation="vertical"
                style={{ width: '1px', height: '100%' }}
              />
            </Grid>
            <Grid size={{ xs: 10, md: 5, lg: 5 }}>
              <NewBoardForm />
            </Grid>
          </Grid>
        </Box>
      </Stack>
    </>
  );
};
