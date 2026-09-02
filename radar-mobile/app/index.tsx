import { useCallback, useEffect, useState } from 'react';
import {
  ActivityIndicator,
  FlatList,
  Pressable,
  RefreshControl,
  SafeAreaView,
  StyleSheet,
  Text,
  View
} from 'react-native';
import * as Location from 'expo-location';

import { fetchRadar } from '@/api/radarApi';
import { ZoneCard } from '@/components/ZoneCard';
import { RadarItem } from '@/model/RadarItem';

export default function RadarScreen() {
  const [position, setPosition] = useState<Location.LocationObject | null>(null);
  const [items, setItems] = useState<RadarItem[]>([]);
  const [loadingLocation, setLoadingLocation] = useState(true);
  const [refreshing, setRefreshing] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [lastUpdated, setLastUpdated] = useState<Date | null>(null);

  const loadRadar = useCallback(async (location: Location.LocationObject) => {
    try {
      setError(null);

      const radar = await fetchRadar({
        lat: location.coords.latitude,
        lon: location.coords.longitude
      });

      setItems(radar);
      setLastUpdated(new Date());
    } catch (e) {
      setError(e instanceof Error ? e.message : 'Failed to load radar');
    }
  }, []);

  const obtainLocation = useCallback(async () => {
    try {
      setLoadingLocation(true);
      setError(null);

      const permission = await Location.requestForegroundPermissionsAsync();

      if (permission.status !== 'granted') {
        setError('Location permission is required for Bolt Radar.');
        return;
      }

      const location = await Location.getCurrentPositionAsync({
        accuracy: Location.Accuracy.High
      });

      setPosition(location);
      await loadRadar(location);
    } catch (e) {
      setError(e instanceof Error ? e.message : 'Failed to read location');
    } finally {
      setLoadingLocation(false);
    }
  }, [loadRadar]);

  useEffect(() => {
    void obtainLocation();

    let subscription: Location.LocationSubscription | undefined;

    void Location.watchPositionAsync(
      {
        accuracy: Location.Accuracy.High,
        distanceInterval: 250,
        timeInterval: 30_000
      },
      location => {
        setPosition(location);
        void loadRadar(location);
      }
    ).then(result => {
      subscription = result;
    });

    return () => subscription?.remove();
  }, [loadRadar, obtainLocation]);

  const refresh = useCallback(async () => {
    if (!position) {
      await obtainLocation();
      return;
    }

    try {
      setRefreshing(true);
      await loadRadar(position);
    } finally {
      setRefreshing(false);
    }
  }, [loadRadar, obtainLocation, position]);

  const locationText = position
    ? `${position.coords.latitude.toFixed(5)}, ${position.coords.longitude.toFixed(5)}`
    : 'Location unavailable';

  return (
    <SafeAreaView style={styles.screen}>
      <View style={styles.header}>
        <Text style={styles.title}>🚕 Bolt Radar</Text>
        <Text style={styles.location}>📍 {locationText}</Text>
        <Text style={styles.updated}>
          {lastUpdated ? `Updated ${lastUpdated.toLocaleTimeString()}` : 'Waiting for first update'}
        </Text>
      </View>

      {loadingLocation && items.length === 0 ? (
        <View style={styles.center}>
          <ActivityIndicator size="large" />
          <Text style={styles.muted}>Reading GPS and loading radar…</Text>
        </View>
      ) : error ? (
        <View style={styles.center}>
          <Text style={styles.error}>{error}</Text>
          <Pressable style={styles.button} onPress={() => void obtainLocation()}>
            <Text style={styles.buttonText}>Try again</Text>
          </Pressable>
        </View>
      ) : (
        <FlatList
          data={items}
          keyExtractor={item => item.zoneCode}
          contentContainerStyle={styles.list}
          renderItem={({ item, index }) => (
            <ZoneCard item={item} recommended={index === 0} />
          )}
          refreshControl={
            <RefreshControl refreshing={refreshing} onRefresh={() => void refresh()} />
          }
          ListEmptyComponent={
            <View style={styles.center}>
              <Text style={styles.muted}>No demand snapshots yet.</Text>
            </View>
          }
        />
      )}
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  screen: { flex: 1, backgroundColor: '#0b1020' },
  header: { paddingHorizontal: 16, paddingTop: 12, paddingBottom: 10 },
  title: { color: '#ffffff', fontSize: 28, fontWeight: '800' },
  location: { color: '#cbd5e1', marginTop: 6, fontSize: 14 },
  updated: { color: '#64748b', marginTop: 3, fontSize: 12 },
  list: { padding: 12, paddingBottom: 32 },
  center: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
    gap: 14,
    padding: 24
  },
  muted: { color: '#94a3b8', textAlign: 'center' },
  error: { color: '#fca5a5', textAlign: 'center' },
  button: {
    backgroundColor: '#2563eb',
    paddingHorizontal: 18,
    paddingVertical: 12,
    borderRadius: 10
  },
  buttonText: { color: '#ffffff', fontWeight: '700' }
});
