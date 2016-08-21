DROP TABLE IF EXISTS public.car CASCADE;
DROP TABLE IF EXISTS public.car_info;

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
