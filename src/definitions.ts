export interface SendIntentPlugin {
  checkSendIntentReceived(): Promise<{ text: string }>;
}
