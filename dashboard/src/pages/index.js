import * as React from 'react'
import axios from 'axios'
import Layout from "../components/layout";
import { Link } from 'gatsby';
import { useTable } from 'react-table';


function Table({ columns, data }) {
    // Use the state and functions returned from useTable to build your UI
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
  
    // Render the UI for your table
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

class IndexQuery extends React.Component {
    columns = [
        {
            Header: 'Name',
            accessor: 'name',
        },
        {
            Header: 'Email',
            accessor: 'email',
        },
    ]    
    state = {
        users: []
    }
    loadUsers = async () => {;
        const users = await axios.get("http://user.ulake.sontg.net/api/user");
        if (users.data.code === 200) {
            let extractUsers = users.data.resp.map(user => {return {name: user.userName, email: user.email}});
            this.setState({users: extractUsers});
        }
    }

    componentDidMount() {
        this.loadUsers();
    }

    render() {
        const {users} = this.state;
        return (
            <Layout pageTitle="Home Page">
                <p>List of available users</p>
                <ul>
                {users.map(user => (
                    <li key={user.name}>
                        <Link to={`./user/${user.name}`}>
                            {user.name}, {user.email}
                        </Link>
                    </li>
                ))}
                </ul>
                <Table columns={this.columns} data={users} />
            </Layout>
        )
    }
}

export default IndexQuery