import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { AuthService } from '../services/auth.service';

export const AuthInterceptor: HttpInterceptorFn = (req, next) => {
  const authService = inject(AuthService);
  const token = authService.getToken();

  console.log('🔐 Interceptor - Request URL:', req.url);
  console.log('🔐 Interceptor - Token from service:', token);

  // Skip adding Authorization header for auth endpoints (login, register, verify)
  if (req.url.includes('/auth/login') || req.url.includes('/auth/register') || req.url.includes('/auth/verify')) {
    console.log('🔐 Interceptor - Skipping Authorization header for auth endpoint');
    return next(req);
  }

  if (token) {
    // Check if token already has Bearer prefix
    const authHeader = token.startsWith('Bearer ') ? token : `Bearer ${token}`;

    console.log('🔐 Interceptor - Final auth header:', authHeader);

    const authReq = req.clone({
      headers: req.headers.set('Authorization', authHeader)
    });

    console.log('🔐 Interceptor - All request headers:', authReq.headers);
    return next(authReq);
  }

  console.log('❌ Interceptor - No token found, proceeding without Authorization header');
  return next(req);
};
