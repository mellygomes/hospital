package br.com.control;

//@author emanuelly.queiroz

import br.com.model.Paciente;
import com.mysql.jdbc.PreparedStatement;
import com.mysql.jdbc.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class ManterPaciente extends DAO{
    
    //@param a
    //@throws Exception    
    public void inserir(Paciente a) throws Exception {
        try {
            abrirBanco();
            con.setAutoCommit(false);
            String queryPaciente = "INSERT INTO paciente(ID_paciente, cpf_paciente, rg_paciente, nome_paciente, nascimento_paciente, telefone_paciente, nome_social_paciente, sexo_paciente)" 
                    + "values(null,?,?,?,?,?,?,?)";
            pst = con.prepareStatement(queryPaciente, Statement.RETURN_GENERATED_KEYS);
            pst.setInt(1, a.getCpf());
            pst.setInt(2, a.getRg());
            pst.setString(3, a.getNome());
            pst.setString(4, a.getNascimento());
            pst.setInt(5, a.getTelefone());
            pst.setString(6, a.getNome_social());
            pst.setString(7, a.getSexo());
            pst.executeUpdate();
                                    
            rs = pst.getGeneratedKeys();
            if (rs.next()) {
                int pacienteId = rs.getInt(1);  // O ID do paciente inserido
                
                if (pacienteId > 0) {
                    // 2. Inserir endereço na tabela endereco
                    String queryEndereco = "INSERT INTO endereco(ID_endereco, rua, numero, bairro, cidade, uf, ID_endereco_paciente)"
                            + " VALUES (null,?,?,?,?,?,?)";
                    pst = con.prepareStatement(queryEndereco);
                    pst.setString(1, a.getRua());
                    pst.setInt(2, a.getNumero());
                    pst.setString(3, a.getBairro());
                    pst.setString(4, a.getCidade());
                    pst.setString(5, a.getUf());
                    pst.setInt(6, pacienteId);  // Definindo a FK (ID do paciente)

                    int rowsEndereco = pst.executeUpdate();
                    
                    if (rowsEndereco > 0) {
                        // Confirmar a transação
                        con.commit();
                        System.out.println("Paciente inserido com sucesso!");
                    } else {
                        // Caso a inserção do endereço falhe, desfazemos a transação
                        con.rollback();
                        System.out.println("Erro ao inserir endereço. Transação revertida.");
                    }
                    
                } else {
                    // Caso o ID do paciente seja inválido (menor ou igual a zero), desfazemos a transação
                    con.rollback();
                    System.out.println("Erro ao obter o ID do paciente. Transação revertida.");
                } 
            } else {
                // Caso não tenha sido gerado um ID (por algum erro interno do banco)
                con.rollback();
                System.out.println("Erro ao obter a chave gerada para o paciente. Transação revertida.");
            }
            
            fecharBanco();
            
        } catch (Exception e){
            System.out.println("Erro" + e.getMessage());
        }       
    }
    
    public ArrayList<Paciente> listarPacientes () throws Exception {
        ArrayList<Paciente> pacientes = new ArrayList<>();
        try{
            abrirBanco();  
            String query = "select nome_paciente, cpf_paciente FROM paciente";
            pst = (PreparedStatement) con.prepareStatement(query);
            ResultSet tr = pst.executeQuery();
            Paciente a;
            
            while (tr.next()){               
              a = new Paciente();
              a.setNome(tr.getString("nome_paciente"));
              a.setCpf(tr.getInt("cpf_paciente"));
              pacientes.add(a);
            } 
            fecharBanco();
            
        } catch (Exception e){
          System.out.println("Erro " + e.getMessage());
        } 
        
       return pacientes;
    }
    
    public void excluir(Paciente p) throws Exception {
        
        try {
            abrirBanco();
            String query = "delete from paciente where cpf_paciente = ?";
            pst = (PreparedStatement) con.prepareStatement(query);
            pst.setInt(1, p.getCpf());
            pst.executeUpdate();
            System.out.println("Deletado!");
            fecharBanco();
        } catch (SQLException e){
            System.out.println("Erro "+ e.getMessage());
        }
        
    }
}
