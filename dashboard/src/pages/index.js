import React, { useEffect, useState } from 'react'
import axios from 'axios'
import Layout from "../components/layout";
import { Link } from 'gatsby';
import { useTable } from 'react-table';

const columns = [{
        Header: 'Name',
        accessor: 'name',
    },
    {
        Header: 'Email',
        accessor: 'email',
    },
];

function Table({ columns, data }) {
    const {
        getTableProps,
        getTableBodyProps,
        headerGroups,
        rows,
        prepareRow,
    } = useTable({
        columns,
        data,
    })

    return (
        <table {...getTableProps()}>
            <thead>
                {headerGroups.map(headerGroup => (
                    <tr {...headerGroup.getHeaderGroupProps()}>
                        {headerGroup.headers.map(column => (
                            <th {...column.getHeaderProps()}>{column.render('Header')}</th>
                        ))}
                    </tr>
                ))}
            </thead>
            <tbody {...getTableBodyProps()}>
                {rows.map((row, i) => {
                    prepareRow(row)
                    return (
                        <tr {...row.getRowProps()}>
                            {row.cells.map(cell => {
                            return <td {...cell.getCellProps()}>{cell.render('Cell')}</td>
                            })}
                        </tr>
                    )
                })}
            </tbody>
        </table>
    )
}

async function fetchUsers(url, setUsers) {
    const ajaxUsers = await axios.get(url);
    if (ajaxUsers.data.code === 200) {            
        const nameEmails = ajaxUsers.data.resp.map(user => {return {name: user.userName, email: user.email}});
        setUsers(nameEmails);
    }
}

function Index() {
    const [users, setUsers] = useState([]);
    fetchUsers("http://user.ulake.sontg.net/api/user", setUsers);

    return (
        <Layout pageTitle="Home Page">
            <p>List of available users</p>
            <ul>
                {users.map(user => (
                <li key={user.name}>
                    <Link to={`./user/${user.name}`}>{user.name}, {user.email}</Link>
                </li>
                ))}
            </ul>
            <Table columns={columns} data={users} />
        </Layout>
    )
}

export default Index