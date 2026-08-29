import { createThirdwebClient } from "thirdweb";
import { polygonAmoy } from "thirdweb/chains";

export const THIRDWEB_CLIENT_ID = import.meta.env.VITE_THIRDWEB_CLIENT_ID || "";
export const THIRDWEB_SECRET_KEY = import.meta.env.VITE_THIRDWEB_SECRET_KEY || "";
export const CONTRACT_ADDRESS = (import.meta.env.VITE_CONTRACT_ADDRESS || "0x052dDa611de283Bcb37C3BCC1c7d1067cF5B38d4") as `0x${string}`;
export const CHAIN = polygonAmoy;

export const thirdwebClient = createThirdwebClient({
  clientId: THIRDWEB_CLIENT_ID,
});
