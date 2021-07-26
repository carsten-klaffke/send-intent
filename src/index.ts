import { registerPlugin } from "@capacitor/core";
import type { SendIntentPlugin } from "./definitions";

const SendIntent = registerPlugin<SendIntentPlugin>("SendIntent", {
  web: () => import("./web").then((m) => new m.SendIntentWeb()),
});

export * from "./definitions";
export { SendIntent };
