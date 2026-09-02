import { Stack } from 'expo-router';
import { StatusBar } from 'expo-status-bar';

export default function RootLayout() {
  return (
    <>
      <StatusBar style="light" />
      <Stack
        screenOptions={{
          headerStyle: { backgroundColor: '#111827' },
          headerTintColor: '#ffffff',
          contentStyle: { backgroundColor: '#0b1020' }
        }}
      >
        <Stack.Screen name="index" options={{ title: 'Tallinn Radar' }} />
      </Stack>
    </>
  );
}
