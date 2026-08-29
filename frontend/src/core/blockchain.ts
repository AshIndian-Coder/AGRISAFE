import { thirdwebClient, CONTRACT_ADDRESS, CHAIN } from "./thirdweb";
import { getContract, prepareContractCall, sendTransaction } from "thirdweb";
import { inAppWallet, type Account } from "thirdweb/wallets";
import { preAuthenticate } from "thirdweb/wallets/in-app";
import { readContract } from "thirdweb";
import { supplyChainABI } from "./abi";

export function getSupplyChainContract() {
  return getContract({
    client: thirdwebClient,
    address: CONTRACT_ADDRESS,
    chain: CHAIN,
    abi: supplyChainABI,
  });
}

export async function sendEmailOtp(email: string) {
  await preAuthenticate({
    client: thirdwebClient,
    strategy: "email",
    email,
  });
}

export async function verifyEmailOtp(email: string, verificationCode: string): Promise<Account> {
  const wallet = inAppWallet();
  const account = await wallet.connect({
    client: thirdwebClient,
    strategy: "email",
    email,
    verificationCode,
  });
  return account;
}

export async function connectInAppWallet(email?: string) {
  if (email) {
    return verifyEmailOtp(email, "");
  }
  const wallet = inAppWallet();
  const account = await wallet.connect({
    client: thirdwebClient,
    strategy: "guest",
  });
  return account;
}

export async function autoConnectWallet(): Promise<Account | null> {
  try {
    const wallet = inAppWallet();
    const account = await wallet.autoConnect({
      client: thirdwebClient,
    });
    return account ?? null;
  } catch {
    return null;
  }
}

export async function createRequest(
  account: Account,
  materialName: string,
  quantity: number,
  metadata: string,
) {
  const contract = getSupplyChainContract();
  const tx = prepareContractCall({
    contract,
    method: "createRequest",
    params: [materialName, BigInt(quantity), metadata],
  });
  return sendTransaction({ transaction: tx, account });
}

export async function deleteRequest(
  account: Account,
  requestId: number,
) {
  const contract = getSupplyChainContract();
  const tx = prepareContractCall({
    contract,
    method: "deleteRequest",
    params: [BigInt(requestId)],
  });
  return sendTransaction({ transaction: tx, account });
}

export async function farmerToSupplier(
  account: Account,
  requestId: number,
  supplierAddress: string,
) {
  const contract = getSupplyChainContract();
  const tx = prepareContractCall({
    contract,
    method: "farmerToSupplier",
    params: [BigInt(requestId), supplierAddress as `0x${string}`],
  });
  return sendTransaction({ transaction: tx, account });
}

export async function supplierToManufacturer(
  account: Account,
  rawBatchIds: number[],
  manufacturerAddress: string,
) {
  const contract = getSupplyChainContract();
  const tx = prepareContractCall({
    contract,
    method: "supplierToManufacturer",
    params: [
      rawBatchIds.map((id) => BigInt(id)),
      manufacturerAddress as `0x${string}`,
    ],
  });
  return sendTransaction({ transaction: tx, account });
}

export async function recordManufacturerInspection(
  account: Account,
  rawBatchId: number,
  result: string,
  metadata: string,
) {
  const contract = getSupplyChainContract();
  const tx = prepareContractCall({
    contract,
    method: "recordManufacturerInspection",
    params: [BigInt(rawBatchId), result, metadata],
  });
  return sendTransaction({ transaction: tx, account });
}

export async function createManufacturedBatch(
  account: Account,
  rawBatchIds: number[],
  productName: string,
  quantity: number,
) {
  const contract = getSupplyChainContract();
  const tx = prepareContractCall({
    contract,
    method: "createManufacturedBatch",
    params: [
      rawBatchIds.map((id) => BigInt(id)),
      productName,
      BigInt(quantity),
    ],
  });
  return sendTransaction({ transaction: tx, account });
}

export async function manufacturerToDistributor(
  account: Account,
  manufacturedBatchId: number,
  distributorAddress: string,
) {
  const contract = getSupplyChainContract();
  const tx = prepareContractCall({
    contract,
    method: "manufacturerToDistributor",
    params: [
      BigInt(manufacturedBatchId),
      distributorAddress as `0x${string}`,
    ],
  });
  return sendTransaction({ transaction: tx, account });
}

export async function distributorToRetailer(
  account: Account,
  manufacturedBatchId: number,
  retailerAddress: string,
  quantity: number,
) {
  const contract = getSupplyChainContract();
  const tx = prepareContractCall({
    contract,
    method: "distributorToRetailer",
    params: [
      BigInt(manufacturedBatchId),
      retailerAddress as `0x${string}`,
      BigInt(quantity),
    ],
  });
  return sendTransaction({ transaction: tx, account });
}

export async function recordAuthorityInspection(
  account: Account,
  requestId: number,
  result: string,
  metadata: string,
) {
  const contract = getSupplyChainContract();
  const tx = prepareContractCall({
    contract,
    method: "recordAuthorityInspection",
    params: [BigInt(requestId), result, metadata],
  });
  return sendTransaction({ transaction: tx, account });
}

export async function recallProduct(
  account: Account,
  manufacturedBatchId: number,
  reason: string,
) {
  const contract = getSupplyChainContract();
  const tx = prepareContractCall({
    contract,
    method: "recallProduct",
    params: [BigInt(manufacturedBatchId), reason],
  });
  return sendTransaction({ transaction: tx, account });
}

export interface RawBatch {
  id: number;
  requestId: number;
  farmer: string;
  supplier: string;
  materialName: string;
  quantity: number;
  inspected: boolean;
  manufacturedBatchId: number;
  transferred: boolean;
}

export interface ManufacturedBatch {
  id: number;
  productName: string;
  quantity: number;
  sourceBatchCount: number;
  manufacturer: string;
  distributor: string;
  recalled: boolean;
  distributed: boolean;
}

export interface RetailerAllocation {
  id: number;
  manufacturedBatchId: number;
  retailer: string;
  distributor: string;
  quantity: number;
  timestamp: number;
}

export interface ChainRequest {
  id: number;
  farmer: string;
  materialName: string;
  quantity: number;
  metadata: string;
  active: boolean;
  rawBatchId: number;
}

export async function getRequest(requestId: number): Promise<ChainRequest> {
  const contract = getSupplyChainContract();
  const result = await readContract({
    contract,
    method: "getRequest",
    params: [BigInt(requestId)],
  });
  return {
    id: Number(result[0]),
    farmer: result[1] as string,
    materialName: result[2] as string,
    quantity: Number(result[3]),
    metadata: result[4] as string,
    active: result[5] as boolean,
    rawBatchId: Number(result[6]),
  };
}

export async function getRequestCount(): Promise<number> {
  const contract = getSupplyChainContract();
  const result = await readContract({
    contract,
    method: "getRequestCount",
    params: [],
  });
  return Number(result);
}

export async function getRawBatch(rawBatchId: number): Promise<RawBatch> {
  const contract = getSupplyChainContract();
  const result = await readContract({
    contract,
    method: "getRawBatch",
    params: [BigInt(rawBatchId)],
  });
  return {
    id: Number(result[0]),
    requestId: Number(result[1]),
    farmer: result[2] as string,
    supplier: result[3] as string,
    materialName: result[4] as string,
    quantity: Number(result[5]),
    inspected: result[6] as boolean,
    manufacturedBatchId: Number(result[7]),
    transferred: result[8] as boolean,
  };
}

export async function getRawBatchCount(): Promise<number> {
  const contract = getSupplyChainContract();
  const result = await readContract({
    contract,
    method: "getRawBatchCount",
    params: [],
  });
  return Number(result);
}

export async function getManufacturedBatch(manufacturedBatchId: number): Promise<ManufacturedBatch> {
  const contract = getSupplyChainContract();
  const result = await readContract({
    contract,
    method: "getManufacturedBatch",
    params: [BigInt(manufacturedBatchId)],
  });
  return {
    id: Number(result[0]),
    productName: result[1] as string,
    quantity: Number(result[2]),
    sourceBatchCount: Number(result[3]),
    manufacturer: result[4] as string,
    distributor: result[5] as string,
    recalled: result[6] as boolean,
    distributed: result[7] as boolean,
  };
}

export async function getManufacturedBatchCount(): Promise<number> {
  const contract = getSupplyChainContract();
  const result = await readContract({
    contract,
    method: "getManufacturedBatchCount",
    params: [],
  });
  return Number(result);
}

export async function getRetailerAllocation(allocationId: number): Promise<RetailerAllocation> {
  const contract = getSupplyChainContract();
  const result = await readContract({
    contract,
    method: "getRetailerAllocation",
    params: [BigInt(allocationId)],
  });
  return {
    id: Number(result[0]),
    manufacturedBatchId: Number(result[1]),
    retailer: result[2] as string,
    distributor: result[3] as string,
    quantity: Number(result[4]),
    timestamp: Number(result[5]),
  };
}

export async function getRetailerAllocationCount(): Promise<number> {
  const contract = getSupplyChainContract();
  const result = await readContract({
    contract,
    method: "getRetailerAllocationCount",
    params: [],
  });
  return Number(result);
}

export async function getSourceRawBatches(manufacturedBatchId: number): Promise<number[]> {
  const contract = getSupplyChainContract();
  const result = await readContract({
    contract,
    method: "getSourceRawBatches",
    params: [BigInt(manufacturedBatchId)],
  });
  return (result as bigint[]).map((id) => Number(id));
}

export async function getBatchManufacturerInspections(rawBatchId: number): Promise<number[]> {
  const contract = getSupplyChainContract();
  const result = await readContract({
    contract,
    method: "getBatchManufacturerInspections",
    params: [BigInt(rawBatchId)],
  });
  return (result as bigint[]).map((id) => Number(id));
}

export async function getBatchRetailerAllocations(manufacturedBatchId: number): Promise<number[]> {
  const contract = getSupplyChainContract();
  const result = await readContract({
    contract,
    method: "getBatchRetailerAllocations",
    params: [BigInt(manufacturedBatchId)],
  });
  return (result as bigint[]).map((id) => Number(id));
}

export async function getManufacturerInspection(inspectionId: number) {
  const contract = getSupplyChainContract();
  const result = await readContract({
    contract,
    method: "getManufacturerInspection",
    params: [BigInt(inspectionId)],
  });
  return {
    id: Number(result[0]),
    rawBatchId: Number(result[1]),
    manufacturer: result[2] as string,
    result: result[3] as string,
    metadata: result[4] as string,
    timestamp: Number(result[5]),
  };
}

export async function getAuthorityInspection(inspectionId: number) {
  const contract = getSupplyChainContract();
  const result = await readContract({
    contract,
    method: "getAuthorityInspection",
    params: [BigInt(inspectionId)],
  });
  return {
    id: Number(result[0]),
    requestId: Number(result[1]),
    authority: result[2] as string,
    result: result[3] as string,
    metadata: result[4] as string,
    timestamp: Number(result[5]),
  };
}

export async function getRawBatchManufacturedBatches(rawBatchId: number): Promise<number[]> {
  const contract = getSupplyChainContract();
  const result = await readContract({
    contract,
    method: "getRawBatchManufacturedBatches",
    params: [BigInt(rawBatchId)],
  });
  return (result as bigint[]).map((id) => Number(id));
}
