import { Container, Divider, Typography } from '@mui/material';

export const NotFoundPage = () => {
  return (
    <Container style={{ textAlign: 'center', marginTop: '20px' }}>
      <Typography variant="h1">404</Typography>
      <Divider orientation="vertical" />
      <Typography variant="h5" style={{ marginTop: '20px' }}>
        Page not found
      </Typography>
    </Container>
  );
};
