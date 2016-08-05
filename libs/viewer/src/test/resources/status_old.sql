create table status (
    id varchar(100) primary key not null,
    toolname varchar(100),              -- name of tool
    statuscode int,                     -- statuscode as defined by HOBIT statuscodes
    description varchar(10000),         -- statusdescription as defined by HOBIT statusdescription
    internaldescription varchar(10000), -- a more detailed internal description
    drmaaid varchar(100),               -- job id used by this job within an GRID/CLOUD environment
    created timestamp,
    lastmod timestamp,
    stdout varchar(1000),
    stderr varchar(1000),
    userid varchar(100),                -- user started this job
    cputime int WITH DEFAULT 0,         -- cputime (minutes) consumed by this job
    diskspace int WITH DEFAULT 0,       -- spool diskspace (MB) used by this job
    memory int WITH DEFAULT 0           -- memory (MB) consumed by this job
);


create table users (
    id varchar(100) primary key not null,
    limitclass varchar(100) not null,    -- id of limitation(=resource) class
    password varchar(100) not null,
    authorized varchar(10),
    name varchar(100) not null,
    surname varchar(100) not null,
    title varchar(20),
    mail varchar(100) not null,
    organisation varchar(100),
    phone varchar(100)

);



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

-- concession table
-- - defines concession classes belonging to an user id
create table concessions (
    id varchar(100) not null,
    class varchar(100) not null
);


-- create a view to simply user data access

-- create view user_view (id, surname, name, limit_runs, limit_cpu, limit_diskspace, limit_memory, runs, cputime,diskspace, memory)
-- as select s1.id, s1.surname, s1.name,s2.runs,


-- add some 'example tool runs'
insert into status(id,toolname,statuscode,userid,cputime,diskspace, created, lastmod) values ('example_0','rnafold',600,'mmusterm',200,10,timestamp('2010-11-01','16:00:00'),timestamp('2010-11-01','17:00:00'));
insert into status(id,toolname,statuscode,userid,cputime,diskspace, created, lastmod) values ('example_1','rnafold',600,'juser',10,300,timestamp('2010-11-02','16:00:00'),timestamp('2010-11-02','17:00:00'));
insert into status(id,toolname,statuscode,userid,cputime,diskspace, created, lastmod) values ('example_2','rnafold',600,'mmusterm',1000,1,timestamp('2010-11-03','16:00:00'),timestamp('2010-11-03','17:00:00'));
insert into status(id,toolname,statuscode,userid,cputime,diskspace, created, lastmod) values ('example_3','rnafold',600,'juser',867,4,timestamp('2010-11-04','16:00:00'),timestamp('2010-11-04','17:00:00'));
insert into status(id,toolname,statuscode,userid,cputime,diskspace, created, lastmod) values ('example_4','rnafold',600,'mmusterm',999,6,timestamp('2010-11-05','16:00:00'),timestamp('2010-11-05','17:00:00'));
insert into status(id,toolname,statuscode,userid,cputime,diskspace, created, lastmod) values ('example_5','rnafold',600,'juser',2,99,timestamp('2010-11-05','16:00:00'),timestamp('2010-11-05','17:00:00'));
insert into status(id,toolname,statuscode,userid,cputime,diskspace, created, lastmod) values ('example_6','rnafold',600,'mmusterm',200,10,timestamp('2010-11-06','16:00:00'),timestamp('2010-11-06','17:00:00'));
insert into status(id,toolname,statuscode,userid,cputime,diskspace, created, lastmod) values ('example_7','rnafold',600,'mmusterm',10,300,timestamp('2010-11-06','16:00:00'),timestamp('2010-11-06','17:00:00'));
insert into status(id,toolname,statuscode,userid,cputime,diskspace, created, lastmod) values ('example_8','rnafold',600,'juser',1000,1,timestamp('2010-11-07','16:00:00'),timestamp('2010-11-07','17:00:00'));
insert into status(id,toolname,statuscode,userid,cputime,diskspace, created, lastmod) values ('example_9','rnafold',604,'mmusterm',867,4,timestamp('2010-11-08','16:30:00'),timestamp('2010-11-08','17:00:00'));
insert into status(id,toolname,statuscode,userid,cputime,diskspace, created, lastmod) values ('example_10','rnafold',601,'juser',999,6,timestamp('2010-11-08','16:45:00'),timestamp('2010-11-08','17:00:00'));
insert into status(id,toolname,statuscode,userid,cputime,diskspace, created, lastmod) values ('example_11','rnafold',601,'mmusterm',2,99,timestamp('2010-11-08','17:00:00'),timestamp('2010-11-08','17:00:00'));

-- asume that current timestamp is '2010-11-08','17:00:00' - consider this for running the tests

-- add two limition classes
-- unregistered can't run more than one job in parallel, consumes up to 30 minutes of cpu time, spool
-- data cleaned up after a short period of time
insert into resources values ('unregistered', 1, 30, -1, -1 );

-- registered users can run up to 10 jobs in parallel, consumes up to 10080 cpu minutes  (= 1 cpu week)
-- a week, uses up to 50 MB of spool disk space, currently no limits on memory usage)
insert into resources values ('registered', 10, 10080, 50, -1);

-- add anonymous user
insert into users (id,password, limitclass,name, surname, mail) values(
    'anonymous',
    'NoPassword',
    'unregistered',
    'Anonymous','User',
    'anonymous@user.nomail'
);

-- add example user 'Max Mustermann'
insert into users (id,password, limitclass,name, surname, mail) values(
    'mmusterm',
    'mmusterm',
    'registered',
    'Max', 'Mustermann',
    'max@mustermann.nomail'
);

create view status_mmusterm as select * from status where userid='mmusterm';


-- add example user 'Joe User'
insert into users (id,password, limitclass,name, surname, mail) values(
    'juser',
    'juser',
    'registered',
    'Joe', 'User',
    'joe@mustermann.nomail'
);

create view status_juser as select * from status where userid='juser';