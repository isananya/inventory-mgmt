import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { environment } from '../../../environments/environment';

export const credentialsInterceptor: HttpInterceptorFn = (req, next) => {
  return next(
    req.clone({
      withCredentials: true
    })
  );
};

export const baseUrlInterceptor: HttpInterceptorFn = (req, next) => {
  if (req.url.startsWith('http')) {
    return next(req);
  }
  const apiReq = req.clone({
    url: `${environment.apiUrl}${req.url}`
  });
  return next(apiReq);
};