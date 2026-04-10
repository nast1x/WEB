import { HttpInterceptorFn } from '@angular/common/http';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const credentials = sessionStorage.getItem('basicAuth');

  if (credentials) {
    const authReq = req.clone({
      headers: req.headers.set('Authorization', `Basic ${credentials}`)
    });
    return next(authReq);
  }

  return next(req);
};
