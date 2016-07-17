DROP TABLE IF EXISTS public.car CASCADE;
DROP TABLE IF EXISTS public.car_info;

CREATE TABLE public.car (
    id INTEGER PRIMARY KEY,
    name varchar (
        50 ) );

CREATE TABLE public.car_info (
    id INTEGER PRIMARY KEY,
    info varchar (
        500 ),
    fk_car_info_car INT NOT NULL REFERENCES public.car );
