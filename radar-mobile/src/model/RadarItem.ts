export type RadarItem = {
  zoneCode: string;
  zoneName: string;
  capturedAt: string;
  score: number;
  distanceKm: number;
  travelMinutes: number;
  demand: number;
  pickupMinutes: number;
  driverSupply: number;
  reasons: string[];
};
