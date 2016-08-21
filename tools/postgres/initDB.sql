DROP TABLE IF EXISTS public.car CASCADE;
DROP TABLE IF EXISTS public.car_info;

DROP TABLE IF EXISTS public.user CASCADE;
DROP TABLE IF EXISTS public.authority;

CREATE TABLE public.car (
  id   INTEGER PRIMARY KEY,
  name VARCHAR(50)
);

CREATE TABLE public.car_info (
  id     INTEGER PRIMARY KEY,
  info   VARCHAR(500),
  car_id INT NOT NULL
);
ALTER TABLE public.car_info
  ADD CONSTRAINT fk_car_info_car FOREIGN KEY (car_id) REFERENCES public.car (id);

CREATE TABLE public.user (
  username VARCHAR(50) NOT NULL PRIMARY KEY,
  password VARCHAR(50) NOT NULL,
  enabled  BOOLEAN     NOT NULL
);

CREATE TABLE public.authority (
  username  VARCHAR(50) NOT NULL,
  authority VARCHAR(50) NOT NULL
);
ALTER TABLE public.authority
  ADD CONSTRAINT fk_authority_user FOREIGN KEY (username) REFERENCES public.user (username);
