const rawBaseUrl =
  process.env.EXPO_PUBLIC_API_BASE_URL ?? 'http://localhost:8080';

export const environment = {
  apiBaseUrl: rawBaseUrl.replace(/\/$/, '')
};
