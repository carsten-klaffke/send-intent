declare module "@capacitor/core" {
  interface PluginRegistry {
    SendIntent: SendIntentPlugin;
  }
}

export interface SendIntentPlugin {
  checkSendIntentReceived(): Promise<{text: string}>;
}
