--DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
--
--Copyright 2010-2013 BiBiServ Curator Team, http://bibiserv.cebitec.uni-bielefeld.de,
--All rights reserved.
--
--The contents of this file are subject to the terms of the Common
--Development and Distribution License("CDDL") (the "License"). You
--may not use this file except in compliance with the License. You can
--obtain a copy of the License at http://www.sun.com/cddl/cddl.html
--
--See the License for the specific language governing permissions and
--limitations under the License.  When distributing the software, include
--this License Header Notice in each file.  If applicable, add the following
--below the License Header, with the fields enclosed by brackets [] replaced
-- by your own identifying information:
--
--"Portions Copyrighted 2010-2013 BiBiServ Curator Team"
--
--Contributor(s): Jan Krueger, jkrueger(at)cebitec.uni-bielefeld.de


create table status (
    id varchar(100) primary key not null,
    toolname varchar(100),              -- name of tool
    functionname varchar(100),          -- name of function
    statuscode int,                     -- statuscode as defined by HOBIT statuscodes
    description varchar(10000),         -- statusdescription as defined by HOBIT statusdescription
    internaldescription varchar(10000), -- a more detailed internal description
    drmaaid varchar(100),               -- job id used by this job within an GRID/CLOUD environment
    created timestamp,
    lastmod timestamp,
    stdout varchar(1000),
    stderr varchar(1000),
    userid varchar(100),                -- user started this job
    cputime int WITH DEFAULT -1,        -- cputime (ms) consumed by this job
    diskspace int WITH DEFAULT -1,      -- spool diskspace (MB) used by this job
    memory int WITH DEFAULT -1,         -- memory (KB) consumed by this job
    interfacetype varchar(20),          -- interface which iniated call (WEB|REST)
    callcmdline clob                    -- cmdline as clob
);

create table downloaduploadstatus (
    id varchar(100) primary key not null,
    statusid varchar(100),
    toolname varchar(100),              -- name of tool
    index int, 				-- to identify from tool
    history varchar(10000),             -- statusdescription that is visible all the time
    state varchar(10000),		-- momentarily progress
    created timestamp,
    lastmod timestamp
);

create table item (
    id varchar(100),
    item clob,
    time timestamp,
    bar blob,
    md5 varchar(33),
    type varchar(20) default 'runnable' -- supported values are runnable, linked, default
);

create table modules (
    id varchar(100) primary key not null,
    info clob,
    file blob,
    active smallint default 0
);

create table structure (
    time timestamp,
    content clob
);


create table users (
    id varchar(100) primary key not null,
    limitclass varchar(100) not null,    -- id of limitation(=resource) class
    password varchar(100) not null,
    passwordreset smallint default 0,    -- force reset of password
    enabled smallint default 0,          -- derby doesn't have a boolean type
    name varchar(100) not null,
    surname varchar(100) not null,
    title varchar(20),
    mail varchar(100) not null,
    organisation varchar(100),
    phone varchar(100)
);

create table resetpassword (
	accesskey  varchar(100) primary key not null,
	id varchar(100),
	expired timestamp
);

create table awscredentials (
   keyname varchar(25) not null,
   userid varchar(100) not null,
   accesskey varchar(150) not null,
   secretkey varchar(250) not null,
   sessiontoken varchar(500) not null,
   isset SMALLINT not null		-- 1 for active key, 0 for all other
);

create table authorities (
    id varchar(100) not null,           -- user id
    authority varchar(100) not null     -- a valid value from table authority
);

-- when a user is deleted, delete also corresponding entries in authorities
create trigger users_trigger
	after delete on users
	referencing old as deletedrow
	for each row mode db2sql
	delete from authorities where id=deletedrow.id;


create table authority (
    value varchar(100) not null unique
);

-- when a authority is deleted, delete also corresponding entries in authorities
create trigger authority_trigger 
	after delete on authority 
	referencing old as deletedrow 
	for each row mode db2sql
	delete from authorities where authority=deletedrow.value;


create table responsibilities (           
    id varchar(100) not null,           -- user id
    itemid varchar(100) not null        -- a valid (but not necessary intalled) item id
);

create table news (
    id varchar(100) not null,
    content clob,
    expired timestamp
);

-- create view used for manager authentication
create view manager_auth as select u.id, u.password from users u, authorities a where u.id = a.id and u.enabled=1 and a.authority='ROLE_ADMIN';



-- insert to authority values // DO NOT REMOVE THEM UNTIL YOUR ARE ABSOlUTELY SURE WHAT YOU ARE DOING!
insert into authority values (
    'ROLE_ADMIN'
);

insert into authority values (
    'ROLE_DEVELOPER'
);

insert into authority values (
    'ROLE_USER'
);


-- insert example admin user
insert into users (id,password, limitclass, enabled, name, surname, mail) values (
    'testadmin',
    'b9dfac81fa1e0516aab7feef882c1f97d13ca2cc', -- SHA1 from simplepassword
    'registered',
    1,
    'BiBi','Serv',
    'bibiadm@cebitec.uni-bielefeld.de'
);

insert into authorities values (
    'testadmin', 'ROLE_ADMIN'
);

insert into authorities values (
    'testadmin', 'ROLE_DEVELOPER'
);

insert into authorities values (
    'testadmin', 'ROLE_USER'
);

-- insert example developer user
insert into users (id,password, limitclass, enabled, name, surname, mail) values (
    'testdev',
    'b9dfac81fa1e0516aab7feef882c1f97d13ca2cc', -- simplepassword
    'registered',
    1,
    'BiBi','Dev',
    'bibidev@cebitec.uni-bielefeld.de');


insert into authorities values (
    'testdev', 'ROLE_DEVELOPER'
);
insert into authorities values (
    'testdev', 'ROLE_USER'
);
insert into responsibilities values (
    'testdev', 'rnahybrid'
);
insert into responsibilities values (
    'testdev', 'dialign'
);
insert into responsibilities values (
    'testdev', 'dca'
);
insert into responsibilities values (
    'testdev', 'mmfind'
);


-- insert example standard user
insert into users (id,password, limitclass, enabled, name, surname, mail) values (
    'testuser',
    'b9dfac81fa1e0516aab7feef882c1f97d13ca2cc', -- simplepassword
    'registered',
    1,
    'Joe','User',
    'bibiadm@cebitec.uni-bielefeld.de');

insert into authorities values (
    'testuser', 'ROLE_USER'
);



create view status_admin as select * from status where id='admin';

-- resources table :
--  - defines limit classes
--  - stores resource consumption for each job (resource_id == status_id)
--  - stores summed up resource consumption for each user (resource_id == user_id)
create table resources (
    id varchar(100) primary key not null,
    runs int WITH DEFAULT 0,               -- concurrently possible runs
    cputime int WITH DEFAULT 0,            -- used cputime during a period of time (one week), measured in minutes
    diskspace int WITH DEFAULT 0,          -- used diskspace, measured in MB
    memory  int WITH DEFAULT 0             -- used memory
);

-- add two limition classes
-- unregistered can't run more than one job in parallel, consumes up to 30 minutes of cpu time, spool
-- data cleaned up after a short period of time
insert into resources values ('unregistered', 1, 30, -1, -1 );

-- registered users can run up to 10 jobs in parallel, consumes up to 10080 cpu minutes  (= 1 cpu week)
-- a week, uses up to 50 MB of spool disk space, currently no limits on memory usage)
insert into resources values ('registered', 10, 10080, 50, -1);

    
CREATE TABLE stats_clientinfo (
    sessionid character varying(100) primary key NOT NULL,
    browsername character varying(50) WITH DEFAULT 'unknown',
    browserversion character varying(50) WITH DEFAULT 'unknown',
    device character varying(50) WITH DEFAULT 'unknown',
    ua character varying(200) WITH DEFAULT '',
    os character varying(50) WITH DEFAULT 'unknown',
    ip character varying(100) WITH DEFAULT 'unknown',
    country character varying(3) WITH DEFAULT '??',
    timestamp timestamp WITH DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE stats_clicks (
    sessionid character varying(100) NOT NULL,
    id character varying(100) NOT NULL,
    userid character varying(100) NOT NULL,
    timestamp timestamp WITH DEFAULT CURRENT_TIMESTAMP
);


CREATE TABLE stats_download (
    sessionid character varying(100) NOT NULL,
    id character varying(100) NOT NULL,
    url character varying(1024) NOT NULL,
    name character varying(100) NOT NULL,
    timestamp timestamp WITH DEFAULT CURRENT_TIMESTAMP
 );
 
 CREATE TABLE stats_usage (
    sessionid character varying(100) NOT NULL,
    bibiservid character varying(100) NOT NULL
 );
 
 -- create indezies to speed up db access
 
 CREATE INDEX statusIDX on status (created);
 CREATE INDEX stats_usageIDX on stats_usage(bibiservid,sessionid);
 
