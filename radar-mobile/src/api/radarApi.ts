import { environment } from '@/config/environment';
import { RadarItem } from '@/model/RadarItem';

type Position = {
  lat: number;
  lon: number;
};

export async function fetchRadar(position: Position): Promise<RadarItem[]> {
  const query = new URLSearchParams({
    lat: String(position.lat),
    lon: String(position.lon)
  });

  const url = `${environment.apiBaseUrl}/api/radar?${query}`;

  const response = await fetch(url, {
    headers: {
      Accept: 'application/json'
    }
  });

  if (!response.ok) {
    const body = await response.text();
    throw new Error(
      `Radar API returned HTTP ${response.status}${body ? `: ${body}` : ''}`
    );
  }

  return response.json() as Promise<RadarItem[]>;
}
