const MAPPING_KEY = 'agri_blockchain_ids';

interface BlockchainIdMapping {
  
  lotToRequest: Record<string, number>;
  
  lotToRawBatch: Record<string, number>;
  
  mfgLotToMfgBatch: Record<string, number>;
  
  bundleToAllocation: Record<string, number>;
}

function getMapping(): BlockchainIdMapping {
  try {
    const raw = localStorage.getItem(MAPPING_KEY);
    if (raw) return JSON.parse(raw);
  } catch {  }
  return { lotToRequest: {}, lotToRawBatch: {}, mfgLotToMfgBatch: {}, bundleToAllocation: {} };
}

function saveMapping(mapping: BlockchainIdMapping) {
  try {
    localStorage.setItem(MAPPING_KEY, JSON.stringify(mapping));
  } catch {  }
}

export function mapLotToRequest(lotId: string, requestId: number) {
  const mapping = getMapping();
  mapping.lotToRequest[lotId] = requestId;
  saveMapping(mapping);
}

export function getRequestId(lotId: string): number | null {
  const mapping = getMapping();
  return mapping.lotToRequest[lotId] ?? null;
}

export function mapLotToRawBatch(lotId: string, rawBatchId: number) {
  const mapping = getMapping();
  mapping.lotToRawBatch[lotId] = rawBatchId;
  saveMapping(mapping);
}

export function getRawBatchId(lotId: string): number | null {
  const mapping = getMapping();
  return mapping.lotToRawBatch[lotId] ?? null;
}

export function mapMfgLotToMfgBatch(mfgLotId: string, mfgBatchId: number) {
  const mapping = getMapping();
  mapping.mfgLotToMfgBatch[mfgLotId] = mfgBatchId;
  saveMapping(mapping);
}

export function getMfgBatchId(mfgLotId: string): number | null {
  const mapping = getMapping();
  return mapping.mfgLotToMfgBatch[mfgLotId] ?? null;
}

export function mapBundleToAllocation(bundleId: string, allocationId: number) {
  const mapping = getMapping();
  mapping.bundleToAllocation[bundleId] = allocationId;
  saveMapping(mapping);
}

export function getAllocationId(bundleId: string): number | null {
  const mapping = getMapping();
  return mapping.bundleToAllocation[bundleId] ?? null;
}

export function clearMappings() {
  try {
    localStorage.removeItem(MAPPING_KEY);
  } catch {  }
}
