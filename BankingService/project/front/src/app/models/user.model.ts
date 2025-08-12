export interface User {
  id?: number;
  email: string;
  password: string;
  name: string;
  phone: number;
  enabled?: boolean;
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface RegisterRequest {
  email: string;
  password: string;
  name: string;
  phone: number;
}

export interface AuthResponse {
  token?: string;
  email?: string;
  data?: {
    token?: string;
    email?: string;
  };
  accessToken?: string;
  jwt?: string;
  success?: boolean;
  message?: string;
}
