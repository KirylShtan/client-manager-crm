
FROM node:20-alpine as build
WORKDIR /app
COPY package.json package-lock.json ./
RUN npm install
COPY . .
# Empty = relative /api and /auth in the built bundle (works behind HTTPS)
ARG REACT_APP_API_ORIGIN=
ENV REACT_APP_API_ORIGIN=$REACT_APP_API_ORIGIN
RUN npm run build


FROM nginx:alpine
COPY --from=build /app/build /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]