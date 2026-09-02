import { StyleSheet, Text, View } from 'react-native';
import { RadarItem } from '@/model/RadarItem';

type Props = {
  item: RadarItem;
  recommended: boolean;
};

function scoreIcon(score: number): string {
  if (score >= 80) return '🔥';
  if (score >= 65) return '🟢';
  if (score >= 45) return '🟡';
  return '🔴';
}

export function ZoneCard({ item, recommended }: Props) {
  return (
    <View style={[styles.card, recommended && styles.recommended]}>
      <View style={styles.row}>
        <View style={styles.nameWrap}>
          <Text style={styles.name}>
            {recommended ? '➡️ ' : ''}
            {scoreIcon(item.score)} {item.zoneName}
          </Text>
          <Text style={styles.code}>{item.zoneCode}</Text>
        </View>

        <Text style={styles.score}>{Math.round(item.score)}</Text>
      </View>

      <Text style={styles.metrics}>
        🚗 {item.travelMinutes} min · {item.distanceKm.toFixed(1)} km
      </Text>

      <Text style={styles.metrics}>
        Demand {Math.round(item.demand * 100)}% · Pickup {item.pickupMinutes} min
      </Text>

      <Text style={styles.reasons}>{item.reasons.join(' · ')}</Text>
    </View>
  );
}

const styles = StyleSheet.create({
  card: {
    backgroundColor: '#172033',
    borderRadius: 14,
    padding: 16,
    marginBottom: 10,
    borderWidth: 1,
    borderColor: '#24324a'
  },
  recommended: {
    borderColor: '#22c55e',
    borderWidth: 2
  },
  row: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    gap: 12
  },
  nameWrap: { flex: 1 },
  name: { color: '#f8fafc', fontSize: 18, fontWeight: '800' },
  code: { color: '#64748b', fontSize: 11, marginTop: 2 },
  score: { color: '#ffffff', fontSize: 28, fontWeight: '900' },
  metrics: { color: '#cbd5e1', marginTop: 9, fontSize: 14 },
  reasons: { color: '#94a3b8', marginTop: 10, fontSize: 13, lineHeight: 18 }
});
