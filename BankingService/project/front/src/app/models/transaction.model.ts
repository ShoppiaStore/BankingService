export interface Transaction {
  id?: number;
  accountId: number;
  type: 'DEPOSIT' | 'WITHDRAW';
  amount: number;
  notes?: string;
  transactionDate: Date;
  balanceAfter: number;
}

export interface TransactionRequest {
  cardNumber: string;
  amount: number;
}

export interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
}
