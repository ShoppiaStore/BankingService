export interface Account {
  id?: number;
  cardNumber: string;
  accountName: string;
  balance: number;
  accountType: 'CHECKING' | 'SAVINGS' | 'BUSINESS' | 'CREDIT' | 'Dollar';
  userId?: number;
}

export interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
}
