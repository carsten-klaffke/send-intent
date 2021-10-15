export interface Intent {
  title?: string;
  description?: string;
  type?: string;
  url?: string;
}

export interface SendIntentPlugin {
  checkSendIntentReceived(): Promise<Intent>;
}
