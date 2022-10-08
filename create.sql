create table category (id bigint not null auto_increment, name varchar(255) not null, notes varchar(255), primary key (id)) engine=InnoDB;
create table receipt_product (receipt_id bigint not null, product_article varchar(255) not null,
                            amount bigint not null, price_for_one double precision not null, price_for_line double precision not null,
                            primary key (receipt_id, product_article)) engine=InnoDB;
create table receipt (id bigint not null auto_increment,
                     date date not null, total_price double precision not null, user_ipn varchar(255) not null, primary key (id)) engine=InnoDB;

create table product (article varchar(255) not null, name varchar(255) not null, category_id bigint not null,
                      producer varchar(255) not null,price double precision not null,
                      volume double precision not null, min_age integer not null, notes varchar(255), primary key (article)) engine=InnoDB;

create table user (ipn varchar(255) not null, email varchar(255) not null,password varchar(255) not null, status varchar(255) not null,
                   surname varchar(255) not null, name varchar(255) not null,  middle_name varchar(255),
                   country varchar(255) not null, district varchar(255) not null,city varchar(255) not null, street varchar(255) not null,
                   house varchar(255) not null, note varchar(255),
                   primary key (ipn)) engine=InnoDB;
alter table category add constraint UK_46ccwnsi9409t36lurvtyljak unique (name);
alter table product add constraint UK_jmivyxk9rmgysrmsqw15lqr5b unique (name);
alter table user add constraint UK_ob8kqyqqgmefl0aco34akdtpe unique (email);
alter table receipt_product add constraint FKkbo9pj2viqwyjdodo9xjvf9yq foreign key (product_article) references product (article);
alter table receipt_product add constraint FKmqqwymkqmqib1n64rqble4f34 foreign key (receipt_id) references receipt (id);
alter table receipt add constraint FKryx35k4tnwef91cpo41wqa1b7 foreign key (user_ipn) references user (ipn);
alter table product add constraint FK1mtsbur82frn64de7balymq9s foreign key (category_id) references category (id);
