insert into credit.public.role
values ((select max(id) + 1 from role), false, 'ADMIN');
insert into credit.public.role
values ((select max(id) + 1 from role), true, 'CUSTOMER');