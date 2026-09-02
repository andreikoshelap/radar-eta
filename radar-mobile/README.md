# bolt-radar-mobile

Expo / React Native client for the Tallinn Bolt Radar backend.

## Stack

- Expo SDK 57
- React Native
- TypeScript
- Expo Router
- expo-location
- REST

## Install

```bash
npm install
```

If Expo reports dependency mismatches:

```bash
npx expo install --fix
```

## Configure backend

```bash
cp .env.example .env
```

Production:

```env
EXPO_PUBLIC_API_BASE_URL=https://gatto-piccolo.com
```

The app will call:

```text
GET https://gatto-piccolo.com/api/radar?lat=...&lon=...
```

Android emulator:

```env
EXPO_PUBLIC_API_BASE_URL=http://10.0.2.2:8080
```

Physical Android phone on the same Wi-Fi:

```env
EXPO_PUBLIC_API_BASE_URL=http://192.168.x.x:8080
```

## Start

```bash
npx expo start
```

For the first MVP, Expo Go is enough because only foreground location is used.

## GPS behavior

The app:
- requests foreground permission;
- gets a high-accuracy location;
- calls `/api/radar`;
- refreshes after roughly 250 m of movement or 30 seconds;
- supports pull-to-refresh.

Background location is intentionally postponed until this foreground flow is stable.

## Production topology on Hetzner

```text
Android / iOS
      |
    HTTPS
      |
gatto-piccolo.com
      |
    nginx
      |
Spring Boot :8080
      |
 PostgreSQL
```

Recommended nginx routing:

```text
/api/* -> Spring Boot
```

Keep the mobile app talking only to HTTPS production endpoints.

## Current backend contract

```http
GET /api/radar?lat=59.4369&lon=24.7535
```

## Next milestones

1. category selector: Bolt / Comfort / Priority
2. background location
3. map screen
4. automatic ferry arrivals
5. automatic airport arrivals
6. ride history
7. push recommendations
8. authentication before public exposure
